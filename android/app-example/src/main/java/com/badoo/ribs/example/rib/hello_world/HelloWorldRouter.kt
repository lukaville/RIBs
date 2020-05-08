package com.badoo.ribs.example.rib.hello_world

import android.os.Parcelable
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.RoutingSource
import com.badoo.ribs.core.routing.action.AttachRibRoutingAction.Companion.attach
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.action.RoutingAction.Companion.noop
import com.badoo.ribs.core.routing.configuration.feature.RoutingElement
import com.badoo.ribs.example.rib.hello_world.HelloWorldRouter.Configuration
import com.badoo.ribs.example.rib.hello_world.HelloWorldRouter.Configuration.Content
import com.badoo.ribs.example.rib.hello_world.HelloWorldRouter.Configuration.Permanent
import com.badoo.ribs.example.rib.small.builder.SmallBuilder
import kotlinx.android.parcel.Parcelize

class HelloWorldRouter(
    buildParams: BuildParams<Nothing?>,
    routingSource: RoutingSource<Configuration>,
    private val smallBuilder: SmallBuilder
): Router<Configuration>(
    buildParams = buildParams,
    routingSource = routingSource,
    permanentParts = listOf(Permanent.Small)
) {
    sealed class Configuration : Parcelable {
        sealed class Permanent : Configuration() {
            @Parcelize object Small : Permanent()
        }
        sealed class Content : Configuration() {
            @Parcelize object Default : Content()
        }
    }

    override fun resolve(routing: RoutingElement<Configuration>): RoutingAction =
        when (routing.configuration) {
            Permanent.Small -> attach { smallBuilder.build(it) }
            Content.Default -> noop()
    }
}
