package com.badoo.ribs.core.routing.portal

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.plugin.PluginFactory
import com.badoo.ribs.core.routing.configuration.feature.operation.push
import com.badoo.ribs.core.routing.portal.PortalRouter.Configuration.Content
import io.reactivex.Single

class PortalNode internal constructor(
    buildParams: BuildParams<*>,
    pluginFactory: PluginFactory<Nothing>,
    private val router: PortalRouter
) : Node<Nothing>(
    buildParams = buildParams,
    viewFactory = null,
    pluginFactory = pluginFactory
), Portal {

    override fun showDefault(): Single<Rib> =
        attachWorkflow {
            router.push(Content.Default)
        }

    override fun showInPortal(ancestryInfo: AncestryInfo): Single<Rib> =
        attachWorkflow {
            router.push(Content.Portal(ancestryInfo.configurationChain))
        }
}
