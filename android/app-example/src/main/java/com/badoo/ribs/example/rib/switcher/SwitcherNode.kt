package com.badoo.ribs.example.rib.switcher

import android.util.Log
import android.view.ViewGroup
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.routing.configuration.feature.operation.push
import com.badoo.ribs.example.rib.dialog_example.DialogExample
import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.hello_world.HelloWorld
import com.badoo.ribs.example.rib.switcher.subtree.Configuration.Content
import io.reactivex.Single

class SwitcherNode(
    buildParams: BuildParams<*>,
    viewFactory: ((ViewGroup) -> SwitcherView?)?,
    plugins: List<Plugin<SwitcherView>>,
    private val backStack: SwitcherBackStack
) : Node<SwitcherView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = plugins
), Switcher {
    
    override fun attachHelloWorld(): Single<HelloWorld> =
        attachWorkflow {
            Log.d("WORKFLOW", "Switcher / attachHelloWorld")
            backStack.push(Content.Hello)
        }

    override fun attachDialogExample(): Single<DialogExample> =
        attachWorkflow {
            Log.d("WORKFLOW", "Switcher / attachDialogExample")
            backStack.push(Content.DialogsExample)
        }

    override fun attachFooBar(): Single<FooBar> =
        attachWorkflow {
            Log.d("WORKFLOW", "Switcher / attachFooBar")
            backStack.push(Content.Foo)
        }

    override fun waitForHelloWorld(): Single<HelloWorld> =
        waitForChildAttached<HelloWorld>()
            .doOnSuccess {
                Log.d("WORKFLOW", "Switcher / waitForHelloWorld")
            }

    override fun doSomethingAndStayOnThisNode(): Single<Switcher> =
        executeWorkflow {
            // push wish to feature
            Log.d("WORKFLOW", "Switcher / doSomethingAndStayOnThisNode")
        }
}
