package com.badoo.ribs.tutorials.tutorial4.rib.hello_world

import com.badoo.ribs.android.Text
import com.badoo.ribs.core.Concept
import com.badoo.ribs.tutorials.tutorial4.util.User
import io.reactivex.functions.Consumer

interface HelloWorld : Concept {

    interface Dependency {
        fun user(): User
        fun config(): Config
        fun helloWorldOutput(): Consumer<Output>
    }

    sealed class Output {
        object HelloThere : Output()
    }

    data class Config(
        val welcomeMessage: Text
    )

    class Customisation(
        val viewFactory: HelloWorldView.Factory = HelloWorldViewImpl.Factory()
    )
}
