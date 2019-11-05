package com.badoo.ribs.tutorials.tutorial5.rib.greetings_container.builder

import android.os.Bundle
import com.badoo.ribs.core.Node
import com.badoo.ribs.tutorials.tutorial5.rib.greetings_container.GreetingsContainer
import com.badoo.ribs.tutorials.tutorial5.rib.hello_world.HelloWorld
import com.badoo.ribs.tutorials.tutorial5.rib.option_selector.OptionSelector
import dagger.BindsInstance

@GreetingsContainerScope
@dagger.Component(
    modules = [GreetingsContainerModule::class],
    dependencies = [GreetingsContainer.Dependency::class]
)
internal interface GreetingsContainerComponent :
    HelloWorld.Dependency,
    OptionSelector.Dependency {

    @dagger.Component.Factory
    interface Factory {
        fun create(
            dependency: GreetingsContainer.Dependency,
            @BindsInstance savedInstanceState: Bundle?
        ): GreetingsContainerComponent
    }

    fun node(): Node<Nothing>
}
