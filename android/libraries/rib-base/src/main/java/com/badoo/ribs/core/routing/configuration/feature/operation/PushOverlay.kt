package com.badoo.ribs.core.routing.configuration.feature.operation

import android.os.Parcelable
import com.badoo.ribs.core.routing.configuration.feature.RoutingElement
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature

data class PushOverlay<C : Parcelable>(
    private val configuration: C
) : BackStackOperation<C> {
    override fun isApplicable(backStack: BackStack<C>): Boolean =
        backStack.isNotEmpty() && configuration != backStack.currentOverlay

    override fun invoke(backStack: BackStack<C>): BackStack<C> =
        backStack.replaceLastWith(
            backStack.last().copy(
                overlays = backStack.last().overlays + configuration
            )
        )

    private val BackStack<C>.current: RoutingElement<C>?
        get() = this.lastOrNull()

    private val BackStack<C>.currentOverlay: C?
        get() = current?.overlays?.lastOrNull()

    private fun BackStack<C>.replaceLastWith(replacement: RoutingElement<C>): BackStack<C> =
        toMutableList().apply { set(lastIndex, replacement) }
}

fun <C : Parcelable> BackStackFeature<C>.pushOverlay(configuration: C) {
    accept(BackStackFeature.Operation(PushOverlay(configuration)))
}
