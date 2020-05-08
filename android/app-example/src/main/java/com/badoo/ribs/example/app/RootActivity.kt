package com.badoo.ribs.example.app

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.android.PermissionRequester
import com.badoo.ribs.android.RibActivity
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.builder.BuildContext
import com.badoo.ribs.core.builder.BuildContext.Companion.root
import com.badoo.ribs.core.plugin.DebugControls
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.routing.action.AttachRibRoutingAction.Companion.attach
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.portal.Portal
import com.badoo.ribs.core.routing.portal.PortalBuilder
import com.badoo.ribs.core.routing.portal.PortalRouter
import com.badoo.ribs.core.routing.transition.handler.CrossFader
import com.badoo.ribs.core.routing.transition.handler.Slider
import com.badoo.ribs.core.routing.transition.handler.TransitionHandler
import com.badoo.ribs.dialog.DialogLauncher
import com.badoo.ribs.example.BuildConfig
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.FooBarBuilder
import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.switcher.Switcher
import com.badoo.ribs.example.rib.switcher.builder.SwitcherBuilder
import com.badoo.ribs.example.util.CoffeeMachine
import com.badoo.ribs.example.util.StupidCoffeeMachine
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer

/** The sample app's single activity */
class RootActivity : RibActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_root)
        super.onCreate(savedInstanceState)
    }

    override val rootViewGroup: ViewGroup
        get() = findViewById(R.id.root)

//    private lateinit var workflowRoot: Portal

    override fun createRib(savedInstanceState: Bundle?) =
//        FooBarBuilder(
//            object : FooBar.Dependency {
//                override fun foobarInput(): ObservableSource<FooBar.Input> = PublishRelay.create()
//                override fun foobarOutput(): Consumer<FooBar.Output> = PublishRelay.create()
//                override fun permissionRequester(): PermissionRequester = permissionRequester
//            }
//        ).build(
//            root(null)
//        )

        SwitcherBuilder(
            object : Switcher.Dependency {
                override fun activityStarter(): ActivityStarter = activityStarter
                override fun permissionRequester(): PermissionRequester = permissionRequester
                override fun dialogLauncher(): DialogLauncher = this@RootActivity
                override fun coffeeMachine(): CoffeeMachine = StupidCoffeeMachine()
                override fun portal(): Portal.OtherSide = object : Portal.OtherSide {
                    override fun showContent(remoteNode: Node<*>, remoteConfiguration: Parcelable) {}
                    override fun showOverlay(remoteNode: Node<*>, remoteConfiguration: Parcelable) {}
                }
            }
        ).build(root(null))

//        PortalBuilder(
//            object : Portal.Dependency {
//                override fun defaultRoutingAction(): (Portal.OtherSide) -> RoutingAction = { portal ->
//                    attach { buildSwitcherNode(portal, it) }
//                }
//
//                override fun transitionHandler(): TransitionHandler<PortalRouter.Configuration>? =
//                    TransitionHandler.multiple(
//                        Slider { it.configuration is PortalRouter.Configuration.Content },
//                        CrossFader { it.configuration is PortalRouter.Configuration.Overlay }
//                    )
//
//                private fun buildSwitcherNode(portal: Portal.OtherSide, buildContext: BuildContext): Switcher {
//                    return SwitcherBuilder(
//                        object : Switcher.Dependency {
//                            override fun activityStarter(): ActivityStarter = activityStarter
//                            override fun permissionRequester(): PermissionRequester =
//                                permissionRequester
//
//                            override fun dialogLauncher(): DialogLauncher = this@RootActivity
//                            override fun coffeeMachine(): CoffeeMachine = StupidCoffeeMachine()
//                            override fun portal(): Portal.OtherSide = portal
//                        }
//                    ).build(buildContext)
//                }
//
//                override fun plugins(): List<Plugin> =
//                    listOf(
//                        DebugControls(
//                            debugParentViewGroup = findViewById(R.id.debug_root),
//                            isEnabled = BuildConfig.DEBUG
//                        )
//                    )
//            }
//        ).build(root(savedInstanceState, AppRibCustomisations)).also {
//            workflowRoot = it
//        }

//    override val workflowFactory: (Intent) -> Observable<*>? = {
//        when {
//            // adb shell am start -a "android.intent.action.VIEW" -d "app-example://workflow1"
//            (it.data?.host == "workflow1") -> executeWorkflow1()
//
//            // adb shell am start -a "android.intent.action.VIEW" -d "app-example://workflow2"
//            (it.data?.host == "workflow2") -> executeWorkflow2()
//
//
//            else -> null
//        }
//    }

//    private fun executeWorkflow1(): Observable<*> =
//        switcher()
//            .flatMap { it.attachHelloWorld()}
//            .toObservable()
//
//    @SuppressWarnings("OptionalUnit")
//    private fun executeWorkflow2(): Observable<*> =
//        Observable.combineLatest(
//            switcher()
//                .flatMap { it.doSomethingAndStayOnThisNode() }
//                .toObservable(),
//
//            switcher()
//                .flatMap { it.waitForHelloWorld() }
//                .flatMap { it.somethingSomethingDarkSide() }
//                .toObservable(),
//
//            BiFunction<Switcher, HelloWorld, Unit> { _, _ -> Unit }
//        )
//
//    @Suppress("UNCHECKED_CAST")
//    private fun switcher() =
//        Single
//            .just(workflowRoot)
//            .flatMap { it.showDefault() as Single<Switcher> }
}
