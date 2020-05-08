package com.badoo.ribs.example.rib.small

import android.os.Parcelable
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.RoutingSource
import com.badoo.ribs.core.routing.action.AnchoredAttachRoutingAction.Companion.anchor
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.action.RoutingAction.Companion.noop
import com.badoo.ribs.core.routing.configuration.feature.RoutingElement
import com.badoo.ribs.example.rib.big.builder.BigBuilder
import com.badoo.ribs.example.rib.portal_overlay_test.PortalOverlayTestBuilder
import com.badoo.ribs.example.rib.small.SmallRouter.Configuration
import com.badoo.ribs.example.rib.small.SmallRouter.Configuration.Content
import com.badoo.ribs.example.rib.small.SmallRouter.Configuration.FullScreen
import kotlinx.android.parcel.Parcelize

class SmallRouter(
    buildParams: BuildParams<Nothing?>,
    routingSource: RoutingSource<Configuration>,
    private val bigBuilder: BigBuilder,
    private val portalOverlayTestBuilder: PortalOverlayTestBuilder
): Router<Configuration>(
    buildParams = buildParams,
    routingSource = routingSource,
    permanentParts = emptyList()
) {
    sealed class Configuration : Parcelable {
        sealed class Content : Configuration() {
            @Parcelize object Default : Content()
        }
        sealed class FullScreen : Configuration() {
            @Parcelize object ShowBig : FullScreen()
            @Parcelize object ShowOverlay : FullScreen()
        }
    }

    override fun resolve(routing: RoutingElement<Configuration>): RoutingAction =
        when (routing.configuration) {
            Content.Default -> noop()
            FullScreen.ShowBig -> anchor(node) { bigBuilder.build(it) }
            FullScreen.ShowOverlay -> anchor(node) { portalOverlayTestBuilder.build(it) }
        }
}
