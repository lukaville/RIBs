package com.badoo.ribs.example.rib.small

import com.badoo.ribs.core.Rib
import com.badoo.ribs.customisation.CanProvidePortal
import com.badoo.ribs.customisation.RibCustomisation

interface Small : Rib {

    interface Dependency :
        CanProvidePortal

    class Customisation(
        val viewFactory: SmallView.Factory = SmallViewImpl.Factory()
    ) : RibCustomisation

    interface Workflow
}
