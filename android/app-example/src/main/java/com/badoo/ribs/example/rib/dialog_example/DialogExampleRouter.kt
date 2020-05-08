package com.badoo.ribs.example.rib.dialog_example

import android.os.Parcelable
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.RoutingSource
import com.badoo.ribs.core.routing.action.DialogRoutingAction.Companion.showDialog
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.action.RoutingAction.Companion.noop
import com.badoo.ribs.core.routing.configuration.feature.RoutingElement
import com.badoo.ribs.dialog.DialogLauncher
import com.badoo.ribs.example.rib.dialog_example.DialogExampleRouter.Configuration
import com.badoo.ribs.example.rib.dialog_example.DialogExampleRouter.Configuration.Content
import com.badoo.ribs.example.rib.dialog_example.DialogExampleRouter.Configuration.Overlay
import com.badoo.ribs.example.rib.dialog_example.dialog.LazyDialog
import com.badoo.ribs.example.rib.dialog_example.dialog.RibDialog
import com.badoo.ribs.example.rib.dialog_example.dialog.SimpleDialog
import kotlinx.android.parcel.Parcelize

class DialogExampleRouter(
    buildParams: BuildParams<Nothing?>,
    routingSource: RoutingSource<Configuration>,
    private val dialogLauncher: DialogLauncher,
    private val simpleDialog: SimpleDialog,
    private val lazyDialog: LazyDialog,
    private val ribDialog: RibDialog
): Router<Configuration>(
    buildParams = buildParams,
    routingSource = routingSource
) {

    sealed class Configuration : Parcelable {
        sealed class Content : Configuration() {
            @Parcelize object Default : Content()
        }
        sealed class Overlay : Configuration() {
            @Parcelize object SimpleDialog : Overlay()
            @Parcelize object LazyDialog : Overlay()
            @Parcelize object RibDialog : Overlay()
        }
    }

    // TODO consider configuration id as second parameter
    override fun resolve(routing: RoutingElement<Configuration>): RoutingAction =
        when (routing.configuration) {
            is Content.Default -> noop()
            // TODO can be done with factory to remove first 3 params and make it simpler
            is Overlay.SimpleDialog -> showDialog(routingSource, routing.identifier, dialogLauncher, simpleDialog)
            is Overlay.LazyDialog -> showDialog(routingSource, routing.identifier, dialogLauncher, lazyDialog)
            is Overlay.RibDialog -> showDialog(routingSource, routing.identifier, dialogLauncher, ribDialog)
        }
}
