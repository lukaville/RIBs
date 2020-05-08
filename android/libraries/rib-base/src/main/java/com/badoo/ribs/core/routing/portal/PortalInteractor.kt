package com.badoo.ribs.core.routing.portal

import android.os.Parcelable
import com.badoo.ribs.core.BackStackInteractor
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.configuration.feature.operation.push
import com.badoo.ribs.core.routing.configuration.feature.operation.pushOverlay
import com.badoo.ribs.core.routing.portal.PortalRouter.Configuration
import com.badoo.ribs.core.routing.portal.PortalRouter.Configuration.Content
import com.badoo.ribs.core.routing.portal.PortalRouter.Configuration.Overlay

internal class PortalInteractor(
    buildParams: BuildParams<Nothing?>
) : BackStackInteractor<Configuration, Nothing>(
    buildParams = buildParams,
    initialConfiguration = Content.Default
), Portal.OtherSide {

    override fun showContent(remoteNode: Node<*>, remoteConfiguration: Parcelable) {
        backStack.push(Content.Portal(remoteNode.ancestryInfo.configurationChain + remoteConfiguration))
    }

    override fun showOverlay(remoteNode: Node<*>, remoteConfiguration: Parcelable) {
        backStack.pushOverlay(Overlay.Portal(remoteNode.ancestryInfo.configurationChain + remoteConfiguration))
    }
}
