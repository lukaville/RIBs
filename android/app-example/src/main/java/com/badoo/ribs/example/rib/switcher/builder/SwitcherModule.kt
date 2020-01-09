@file:Suppress("LongParameterList")
package com.badoo.ribs.example.rib.switcher.builder

import android.os.Bundle
import com.badoo.ribs.dialog.DialogLauncher
import com.badoo.ribs.example.rib.blocker.Blocker
import com.badoo.ribs.example.rib.blocker.BlockerBuilder
import com.badoo.ribs.example.rib.dialog_example.builder.DialogExampleBuilder
import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.FooBarBuilder
import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.hello_world.HelloWorldBuilder
import com.badoo.ribs.example.rib.menu.Menu
import com.badoo.ribs.example.rib.menu.MenuBuilder
import com.badoo.ribs.example.rib.switcher.Switcher
import com.badoo.ribs.example.rib.switcher.SwitcherInteractor
import com.badoo.ribs.example.rib.switcher.SwitcherNode
import com.badoo.ribs.example.rib.switcher.SwitcherRouter
import com.badoo.ribs.example.rib.switcher.SwitcherView
import com.badoo.ribs.example.rib.switcher.analytics.ScreenTracker
import com.badoo.ribs.example.rib.switcher.dialog.DialogToTestOverlay
import com.badoo.ribs.example.util.CoffeeMachine
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object SwitcherModule {

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun dialogToTestOverlay(): DialogToTestOverlay =
        DialogToTestOverlay()

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun router(
        savedInstanceState: Bundle?,
        component: SwitcherComponent,
        dialogLauncher: DialogLauncher,
        dialogToTestOverlay: DialogToTestOverlay
    ): SwitcherRouter =
        SwitcherRouter(
            savedInstanceState = savedInstanceState,
            fooBarBuilder = FooBarBuilder(component),
            helloWorldBuilder = HelloWorldBuilder(component),
            dialogExampleBuilder = DialogExampleBuilder(component),
            blockerBuilder = BlockerBuilder(component),
            menuBuilder = MenuBuilder(component),
            dialogLauncher = dialogLauncher,
            dialogToTestOverlay = dialogToTestOverlay
        )

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun interactor(
        savedInstanceState: Bundle?,
        router: SwitcherRouter,
        dialogToTestOverlay: DialogToTestOverlay
    ): SwitcherInteractor =
        SwitcherInteractor(
            savedInstanceState = savedInstanceState,
            router = router,
            dialogToTestOverlay = dialogToTestOverlay
        )

    @Provides
    @JvmStatic
    internal fun viewDependency(
        coffeeMachine: CoffeeMachine
    ): SwitcherView.Dependency =
        object : SwitcherView.Dependency {
            override fun coffeeMachine(): CoffeeMachine = coffeeMachine
        }

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun node(
        savedInstanceState: Bundle?,
        customisation: Switcher.Customisation,
        viewDependency: SwitcherView.Dependency,
        router: SwitcherRouter,
        interactor: SwitcherInteractor
    ) : SwitcherNode = SwitcherNode(
        savedInstanceState = savedInstanceState,
        viewFactory = customisation.viewFactory(viewDependency),
        router = router,
        interactor = interactor,
        screenTracker = ScreenTracker()
    )

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun helloWorldInput(): ObservableSource<HelloWorld.Input> = Observable.empty()

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun helloWorldOutput(): Consumer<HelloWorld.Output> = Consumer { }

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun fooBarInput(): ObservableSource<FooBar.Input> = Observable.empty()

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun fooBarOutput(): Consumer<FooBar.Output> = Consumer { }

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun menuUpdater(
        router: SwitcherRouter
    ): ObservableSource<Menu.Input> =
        router.menuUpdater

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun menuListener(
        interactor: SwitcherInteractor
    ): Consumer<Menu.Output> =
        interactor.MenuListener()

    @SwitcherScope
    @Provides
    @JvmStatic
    internal fun loremIpsumOutputConsumer(
        interactor: SwitcherInteractor
    ): Consumer<Blocker.Output> =
        interactor.loremIpsumOutputConsumer
}
