@file:Suppress("LongParameterList", "LongMethod")
package com.badoo.ribs.example.rib.switcher.builder

import com.badoo.ribs.core.builder.BuildParams
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
import com.badoo.ribs.example.rib.switcher.debug.SwitcherDebugControls
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
        buildParams: BuildParams<Nothing?>,
        component: SwitcherComponent,
        customisation: Switcher.Customisation,
        dialogLauncher: DialogLauncher,
        dialogToTestOverlay: DialogToTestOverlay
    ): SwitcherRouter =
        SwitcherRouter(
            buildParams = buildParams,
            transitionHandler = customisation.transitionHandler,
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
        buildParams: BuildParams<Nothing?>,
        router: SwitcherRouter,
        dialogToTestOverlay: DialogToTestOverlay
    ): SwitcherInteractor =
        SwitcherInteractor(
            buildParams = buildParams,
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
        buildParams: BuildParams<Nothing?>,
        customisation: Switcher.Customisation,
        viewDependency: SwitcherView.Dependency,
        router: SwitcherRouter,
        interactor: SwitcherInteractor
    ): SwitcherNode = SwitcherNode(
        router = router,
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(viewDependency),
        pluginFactory = { listOf(
            interactor,
            router,
            SwitcherDebugControls(it as SwitcherNode)
        )}
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
