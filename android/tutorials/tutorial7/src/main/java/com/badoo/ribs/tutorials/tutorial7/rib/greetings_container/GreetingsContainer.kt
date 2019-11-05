package com.badoo.ribs.tutorials.tutorial7.rib.greetings_container

import com.badoo.ribs.core.Rib
import com.badoo.ribs.tutorials.tutorial7.util.Lexem
import com.badoo.ribs.tutorials.tutorial7.util.User
import io.reactivex.functions.Consumer

interface GreetingsContainer : Rib {

    interface Dependency {
        fun user(): User
        fun greetingsContainerOutput(): Consumer<Output>
    }

    sealed class Output {
        data class GreetingsSaid(val greeting: Lexem) : Output()
    }
}
