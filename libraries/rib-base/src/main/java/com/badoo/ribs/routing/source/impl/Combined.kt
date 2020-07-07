package com.badoo.ribs.routing.source.impl

import android.os.Bundle
import android.os.Parcelable
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.history.RoutingHistory
import com.badoo.ribs.routing.history.RoutingHistoryElement
import com.badoo.ribs.routing.source.RoutingSource
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.functions.BiFunction
import java.util.ArrayDeque

internal data class Combined<C : Parcelable>(
    val first: RoutingSource<C>,
    val second: RoutingSource<C>
) : RoutingSource<C> {

    data class CombinedHistory<C : Parcelable>(
        val first: RoutingHistory<C>,
        val second: RoutingHistory<C>
    ): RoutingHistory<C> {

        override fun iterator(): Iterator<RoutingHistoryElement<C>> =
            ConcatIterator(first.iterator()) + second.iterator()
    }

    private val combined = Observable.combineLatest(
        first,
        second,
        BiFunction<RoutingHistory<C>, RoutingHistory<C>, RoutingHistory<C>> { source1, source2 ->
            CombinedHistory(
                source1,
                source2
            )
        }
    )

    override fun baseLineState(fromRestored: Boolean): RoutingHistory<C> =
        CombinedHistory(
            first.baseLineState(fromRestored),
            second.baseLineState(fromRestored)
        )

    override fun subscribe(observer: Observer<in RoutingHistory<C>>) {
        combined.subscribe(observer)
    }

    override fun remove(identifier: Routing.Identifier) {
        first.remove(identifier)
        second.remove(identifier)
    }

    override fun onTransitionFinished() {
        first.onTransitionFinished()
        second.onTransitionFinished()
    }

    override fun handleBackPressFirst(): Boolean =
        first.handleBackPressFirst() || second.handleBackPressFirst()

    override fun handleBackPressFallback(): Boolean =
        first.handleBackPressFallback() || second.handleBackPressFallback()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        first.onSaveInstanceState(outState)
        second.onSaveInstanceState(outState)
    }

    private class ConcatIterator<T>(iterator: Iterator<T>) : Iterator<T> {
        private val store = ArrayDeque<Iterator<T>>()

        init {
            if (iterator.hasNext()) {
                store.add(iterator)
            }
        }

        override fun hasNext(): Boolean = when {
            store.isEmpty() -> false
            else -> store.first.hasNext()
        }

        override fun next(): T {
            val next = store.first.next()

            if (!store.first.hasNext()) {
                store.removeFirst()
            }

            return next
        }

        operator fun plus(iterator: Iterator<T>): ConcatIterator<T> {
            if (iterator.hasNext()) {
                store.add(iterator)
            }

            return this
        }
    }
}
