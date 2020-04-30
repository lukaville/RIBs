package com.badoo.ribs.example.rib.portal_overlay_test

import com.badoo.ribs.core.builder.BuildParams
import android.view.ViewGroup
import com.badoo.ribs.core.Node

class PortalOverlayTestNode(
    buildParams: BuildParams<*>,
    viewFactory: ((ViewGroup) -> PortalOverlayTestView?)?,
    interactor: PortalOverlayTestInteractor
) : Node<PortalOverlayTestView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    router = null,
    interactor = interactor
), PortalOverlayTest {
    override val node: Node<PortalOverlayTestView> = this
}
