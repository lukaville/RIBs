package com.badoo.ribs.example.rib.hello_world

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.badoo.ribs.core.Node
import io.reactivex.Single

class HelloWorldNode(
    viewFactory: ((ViewGroup) -> HelloWorldView?)?,
    private val router: HelloWorldRouter,
    interactor: HelloWorldInteractor,
    savedInstanceState: Bundle?
) : Node<HelloWorldView>(
    savedInstanceState = savedInstanceState,
    identifier = object : HelloWorld {},
    viewFactory = viewFactory,
    plugins = listOf(interactor, router)
), HelloWorld.Workflow {

    override fun somethingSomethingDarkSide(): Single<HelloWorld.Workflow> =
        executeWorkflow {
            Log.d("WORKFLOW", "Hello world / somethingSomethingDarkSide")
        }
}
