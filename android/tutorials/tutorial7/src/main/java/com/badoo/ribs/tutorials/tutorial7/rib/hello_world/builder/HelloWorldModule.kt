package com.badoo.ribs.tutorials.tutorial7.rib.hello_world.builder

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.tutorials.tutorial7.rib.hello_world.HelloWorld
import com.badoo.ribs.tutorials.tutorial7.rib.hello_world.HelloWorldInteractor
import com.badoo.ribs.tutorials.tutorial7.rib.hello_world.HelloWorldRouter
import com.badoo.ribs.tutorials.tutorial7.rib.hello_world.HelloWorldView
import com.badoo.ribs.tutorials.tutorial7.util.User
import dagger.Provides
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object HelloWorldModule {

    @HelloWorldScope
    @Provides
    @JvmStatic
    internal fun router(): HelloWorldRouter =
        HelloWorldRouter()

    @HelloWorldScope
    @Provides
    @JvmStatic
    internal fun interactor(
        user: User,
        config: HelloWorld.Configuration,
        input: ObservableSource<HelloWorld.Input>,
        output: Consumer<HelloWorld.Output>,
        router: HelloWorldRouter
    ): HelloWorldInteractor =
        HelloWorldInteractor(
            user = user,
            config = config,
            input = input,
            output = output,
            router = router
        )

    @HelloWorldScope
    @Provides
    @JvmStatic
    internal fun node(
        viewFactory: ViewFactory<HelloWorldView>,
        router: HelloWorldRouter,
        interactor: HelloWorldInteractor
    ) : Node<HelloWorldView> = Node(
        identifier = object : HelloWorld {},
        viewFactory = viewFactory,
        router = router,
        interactor = interactor
    )
}
