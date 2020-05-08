package com.badoo.ribs.test.util.ribs.child

import android.os.Parcelable
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.test.util.ribs.child.TestChildRouter.Configuration
import kotlinx.android.parcel.Parcelize

class TestChildRouter(
    buildParams: BuildParams<Nothing?>
): Router<Configuration, Nothing, Configuration, Nothing, TestChildView>(
    buildParams = buildParams,
    initialConfiguration = Configuration.Default
) {
    sealed class Configuration : Parcelable {
        @Parcelize object Default : Configuration()
    }

    override fun resolve(routing: RoutingElement<Configuration>): RoutingAction =
        RoutingAction.noop()
}
