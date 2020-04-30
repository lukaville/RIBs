package com.badoo.ribs.dialog

import com.badoo.ribs.android.Text
import com.badoo.ribs.core.AttachMode
import com.badoo.ribs.core.Concept
import com.badoo.ribs.core.builder.BuildContext
import com.badoo.ribs.core.builder.ConceptFactory
import com.badoo.ribs.dialog.Dialog.CancellationPolicy.NonCancellable
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource

abstract class Dialog<T : Any> private constructor(
    factory: Dialog<T>.() -> Unit,
    private val events: PublishRelay<T>
) : ObservableSource<T> by events {
    var title: Text? = null
    var message: Text? = null
    var cancellationPolicy: CancellationPolicy<T> = NonCancellable()
    internal var buttons: ButtonsConfig<T>? = null
    private var conceptFactory: ConceptFactory? = null
    internal var concept: Concept<*>? = null

    constructor(factory: Dialog<T>.() -> Unit) : this(
        factory,
        PublishRelay.create()
    )

    init {
        factory()
    }

    fun nodeFactory(conceptFactory: ConceptFactory) {
        this.conceptFactory = conceptFactory
    }

    fun buttons(factory: ButtonsConfig<T>.() -> Unit) {
        this.buttons = ButtonsConfig(factory)
    }

    class ButtonsConfig<T : Any>(
        factory: ButtonsConfig<T>.() -> Unit
    ) {
        internal var positive: ButtonConfig<T>? = null
        internal var negative: ButtonConfig<T>? = null
        internal var neutral: ButtonConfig<T>? = null

        init {
            factory()
        }

        fun positive(title: Text, onClickEvent: T, closesDialogAutomatically: Boolean = true) {
            positive = ButtonConfig(title, onClickEvent, closesDialogAutomatically)
        }

        fun negative(title: Text, onClickEvent: T, closesDialogAutomatically: Boolean = true) {
            negative = ButtonConfig(title, onClickEvent, closesDialogAutomatically)
        }

        fun neutral(title: Text, onClickEvent: T, closesDialogAutomatically: Boolean = true) {
            neutral = ButtonConfig(title, onClickEvent, closesDialogAutomatically)
        }

        data class ButtonConfig<T : Any>(
            var title: Text? = null,
            var onClickEvent: T? = null,
            var closesDialogAutomatically: Boolean = true
        )
    }

    fun publish(event: T) {
        events.accept(event)
    }

    fun buildConcepts(buildContext: BuildContext): List<Concept<*>> =
        conceptFactory?.let { factory ->
            val clientParams = buildContext.copy(
                /**
                 * RIBs inside dialogs behaved like Root nodes so far in that they were
                 * not added as a child of any other Node.
                 * Using [AncestryInfo.Child.anchor] it's now also possible to change this,
                 * and rather add the RIB inside the dialog to the parent, if removal is also guaranteed.
                 * A benefit of this would be back press and lifecycle propagation.
                 * Not entirely sure it is needed. To be reconsidered later.
                 */
                attachMode = AttachMode.EXTERNAL
            )

            listOf(
                factory.invoke(clientParams).also {
                    concept = it
                }
            )
        } ?: emptyList()

    sealed class Event {
        object Positive : Event()
        object Negative : Event()
        object Neutral : Event()
        object Cancelled : Event()
    }

    sealed class CancellationPolicy<T : Any> {
        class NonCancellable<T : Any> : CancellationPolicy<T>()
        data class Cancellable<T : Any>(
            val event: T,
            val cancelOnTouchOutside: Boolean
        ) : CancellationPolicy<T>()
    }
}
