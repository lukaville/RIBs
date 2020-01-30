package com.badoo.ribs.example.rib.switcher

import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.badoo.ribs.android.CanProvideActivityStarter
import com.badoo.ribs.android.CanProvidePermissionRequester
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.routing.transition.handler.CrossFader
import com.badoo.ribs.core.routing.transition.handler.SharedElements
import com.badoo.ribs.core.routing.transition.handler.TabSwitcher
import com.badoo.ribs.core.routing.transition.handler.TransitionHandler
import com.badoo.ribs.core.routing.transition.handler.TransitionHandler.Companion.multiple
import com.badoo.ribs.core.routing.transition.sharedelement.SharedElementTransition.Params
import com.badoo.ribs.core.routing.transition.sharedelement.SharedElementTransition.RotationParams
import com.badoo.ribs.customisation.CanProvidePortal
import com.badoo.ribs.customisation.CanProvideRibCustomisation
import com.badoo.ribs.customisation.RibCustomisation
import com.badoo.ribs.dialog.CanProvideDialogLauncher
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.switcher.SwitcherRouter.Configuration.Content.*
import com.badoo.ribs.example.util.CoffeeMachine
import io.reactivex.Single

interface Switcher : Rib {

    interface Dependency :
        CanProvideActivityStarter,
        CanProvidePermissionRequester,
        CanProvideDialogLauncher,
        CanProvideRibCustomisation,
        CanProvidePortal {

        fun coffeeMachine(): CoffeeMachine
    }

    class Customisation(
        val viewFactory: SwitcherView.Factory = SwitcherViewImpl.Factory(),
        val transitionHandler: TransitionHandler<SwitcherRouter.Configuration> =
            multiple<SwitcherRouter.Configuration>(
//                Slider(
//                    duration = 1000
//                )
//                ,
                TabSwitcher(
                    duration = 4000,
                    tabsOrder = listOf(Hello, Foo, DialogsExample)
                )
                ,
                SharedElements(
                    params = listOf(
                        Params(
                            duration = 4000,
                            exitingElementMatcher = { it.findViewById(R.id.sharedElementSquare) },
                            enteringElementMatcher = { it.findViewById(R.id.sharedElementSquare) },
                            translateXInterpolator = LinearInterpolator(),
                            translateYInterpolator = LinearInterpolator()
//                            ,
//                            rotation = RotationParams(0.75f * 360)
                        )
                    )
                )
                ,
                CrossFader(
                    duration = 4000
                )
            )
    ) : RibCustomisation

    interface Workflow {
        fun attachHelloWorld(): Single<HelloWorld.Workflow>
        fun testCrash(): Single<HelloWorld.Workflow>
        fun waitForHelloWorld(): Single<HelloWorld.Workflow>
        fun doSomethingAndStayOnThisNode(): Single<Switcher.Workflow>
    }
}
