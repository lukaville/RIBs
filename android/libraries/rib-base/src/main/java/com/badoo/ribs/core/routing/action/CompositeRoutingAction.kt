package com.badoo.ribs.core.routing.action

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.builder.BuildContext
import com.badoo.ribs.core.view.RibView

class CompositeRoutingAction< V : RibView>(
    private vararg val routingActions: RoutingAction<V>
) : RoutingAction<V> {

    constructor(routingActions: List<RoutingAction<V>>) : this(*routingActions.toTypedArray())

    override fun buildNodes(buildContexts: List<BuildContext>) : List<Node<*>> =
        routingActions.mapIndexed { index, routingAction ->
            routingAction.buildNodes(
                buildContexts = buildContexts.getOrNull(index)?.let { listOf(it) } ?: emptyList()
            )
        }.flatten()

    override fun execute() {
        routingActions.forEach {
            it.execute()
        }
    }

    override fun cleanup() {
        routingActions.forEach {
            it.cleanup()
        }
    }

    companion object {
        fun < V : RibView> composite(vararg routingActions: RoutingAction<V>): RoutingAction<V> =
            CompositeRoutingAction(*routingActions)
    }
}
