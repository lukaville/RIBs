package com.badoo.ribs.example.rib.hello_world

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.badoo.ribs.core.Node
import com.badoo.ribs.example.util.CanReportViewScreen
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
    router = router,
    interactor = interactor
), HelloWorld.Workflow, CanReportViewScreen {

    override val screenName: String
        get() = "SCREEN_NAME_HELLO_WORLD"

    override fun somethingSomethingDarkSide(): Single<HelloWorld.Workflow> =
        executeWorkflow {
            Log.d("WORKFLOW", "Hello world / somethingSomethingDarkSide")
        }
}
