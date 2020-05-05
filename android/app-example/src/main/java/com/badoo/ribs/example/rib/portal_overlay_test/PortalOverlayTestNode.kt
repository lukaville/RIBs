package com.badoo.ribs.example.rib.portal_overlay_test

import android.view.ViewGroup
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.builder.BuildParams

class PortalOverlayTestNode(
    buildParams: BuildParams<*>,
    viewFactory: ((ViewGroup) -> PortalOverlayTestView?)?,
    interactor: PortalOverlayTestInteractor
) : Node<PortalOverlayTestView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = listOf(interactor)
), PortalOverlayTest {

}
