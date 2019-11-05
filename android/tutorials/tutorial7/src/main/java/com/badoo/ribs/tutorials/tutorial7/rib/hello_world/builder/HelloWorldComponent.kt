package com.badoo.ribs.tutorials.tutorial7.rib.hello_world.builder

import com.badoo.ribs.core.Node
import com.badoo.ribs.tutorials.tutorial7.rib.hello_world.HelloWorld
import com.badoo.ribs.tutorials.tutorial7.rib.hello_world.HelloWorldView

@HelloWorldScope
@dagger.Component(
    modules = [HelloWorldModule::class],
    dependencies = [
        HelloWorld.Dependency::class,
        HelloWorld.Customisation::class
    ]
)
internal interface HelloWorldComponent {

    @dagger.Component.Builder
    interface Builder {

        fun dependency(component: HelloWorld.Dependency): Builder

        fun customisation(component: HelloWorld.Customisation): Builder

        fun build(): HelloWorldComponent
    }

    fun node(): Node<HelloWorldView>
}
