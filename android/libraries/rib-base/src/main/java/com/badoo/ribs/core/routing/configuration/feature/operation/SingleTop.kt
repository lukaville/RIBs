package com.badoo.ribs.core.routing.configuration.feature.operation

import android.os.Parcelable
import com.badoo.ribs.core.routing.configuration.feature.RoutingElement
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature.Operation

data class SingleTop<C : Parcelable>(
    private val configuration: C
) : BackStackOperation<C> {
    override fun isApplicable(backStack: BackStack<C>): Boolean =
        true

    override fun invoke(backStack: BackStack<C>): BackStack<C> {
        val targetClass = configuration.javaClass
        val lastIndexOfSameClass = backStack.indexOfLast {
            targetClass.isInstance(it.configuration)
        }

        val operation: (BackStack<C>) -> BackStack<C> =
            if (lastIndexOfSameClass == -1) {
                Push(configuration)
            } else {
                if (backStack[lastIndexOfSameClass] == configuration) {
                    SingleTopReactivateBackStackOperation(lastIndexOfSameClass)
                } else {
                    SingleTopReplaceBackStackOperation(configuration, lastIndexOfSameClass)
                }
            }

        return operation.invoke(backStack)
    }

    private class SingleTopReactivateBackStackOperation<C : Parcelable>(
        private val position: Int
    ) : (BackStack<C>) -> BackStack<C> {

        override fun invoke(backStack: BackStack<C>): BackStack<C> =
            backStack.dropLast(backStack.size - position - 1)
    }

    private class SingleTopReplaceBackStackOperation<C : Parcelable>(
        private val configuration: C,
        private val position: Int
    ) : (BackStack<C>) -> BackStack<C> {

        override fun invoke(backStack: BackStack<C>): BackStack<C> =
            backStack.dropLast(backStack.size - position) + RoutingElement(configuration)
    }
}

fun <C : Parcelable> BackStackFeature<C>.singleTop(configuration: C) {
    accept(Operation(SingleTop(configuration)))
}
