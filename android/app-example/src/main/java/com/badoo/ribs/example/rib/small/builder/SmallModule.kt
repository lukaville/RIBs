@file:SuppressWarnings("LongParameterList", "LongMethod")
package com.badoo.ribs.example.rib.small.builder

import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.portal.Portal
import com.badoo.ribs.example.rib.big.builder.BigBuilder
import com.badoo.ribs.example.rib.portal_overlay_test.PortalOverlayTestBuilder
import com.badoo.ribs.example.rib.small.Small
import com.badoo.ribs.example.rib.small.SmallInteractor
import com.badoo.ribs.example.rib.small.SmallNode
import com.badoo.ribs.example.rib.small.SmallRouter
import dagger.Provides

@dagger.Module
internal object SmallModule {

    @SmallScope
    @Provides
    @JvmStatic
    internal fun router(
        // pass component to child rib builders, or remove if there are none
        component: SmallComponent,
        buildParams: BuildParams<Nothing?>
    ): SmallRouter =
        SmallRouter(
            buildParams = buildParams,
            bigBuilder = BigBuilder(component),
            portalOverlayTestBuilder = PortalOverlayTestBuilder(component)
        )

    @SmallScope
    @Provides
    @JvmStatic
    internal fun interactor(
        buildParams: BuildParams<Nothing?>,
        router: SmallRouter,
        portal: Portal.OtherSide
    ): SmallInteractor =
        SmallInteractor(
            buildParams = buildParams,
            router = router,
            portal = portal
        )

    @SmallScope
    @Provides
    @JvmStatic
    internal fun node(
        buildParams: BuildParams<Nothing?>,
        customisation: Small.Customisation,
        router: SmallRouter,
        interactor: SmallInteractor
    ) : SmallNode = SmallNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(null),
        pluginFactory = { listOf(
            interactor, router
        )}
    )
}
