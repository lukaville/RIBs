package com.badoo.ribs.template.node_dagger.foo_bar

import android.os.Parcelable
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.transition.handler.TransitionHandler
import com.badoo.ribs.template.node_dagger.foo_bar.FooBarRouter.Configuration
import com.badoo.ribs.template.node_dagger.foo_bar.FooBarRouter.Configuration.Content
import com.badoo.ribs.template.node_dagger.foo_bar.FooBarRouter.Configuration.Overlay
import com.badoo.ribs.template.node_dagger.foo_bar.FooBarRouter.Configuration.Permanent
import kotlinx.android.parcel.Parcelize

class FooBarRouter(
    buildParams: BuildParams<*>,
    transitionHandler: TransitionHandler<Configuration>? = null
): Router<Configuration, Permanent, Content, Overlay, FooBarView>(
    buildParams = buildParams,
    transitionHandler = transitionHandler,
    initialConfiguration = Content.Default,
    permanentParts = emptyList()
) {
    sealed class Configuration : Parcelable {
        sealed class Permanent : Configuration()
        sealed class Content : Configuration() {
            @Parcelize object Default : Content()
        }
        sealed class Overlay : Configuration()
    }

    override fun resolve(routing: RoutingElement<Configuration>): RoutingAction<FooBarView> =
        RoutingAction.noop()
}
