package com.badoo.ribs.tutorials.tutorial5.rib.greetings_container.builder

import android.os.Bundle
import com.badoo.ribs.core.Node
import com.badoo.ribs.tutorials.tutorial5.R
import com.badoo.ribs.tutorials.tutorial5.rib.greetings_container.GreetingsContainer
import com.badoo.ribs.tutorials.tutorial5.rib.greetings_container.GreetingsContainerInteractor
import com.badoo.ribs.tutorials.tutorial5.rib.greetings_container.GreetingsContainerRouter
import com.badoo.ribs.tutorials.tutorial5.rib.hello_world.HelloWorld
import com.badoo.ribs.tutorials.tutorial5.rib.hello_world.builder.HelloWorldBuilder
import com.badoo.ribs.tutorials.tutorial5.rib.option_selector.OptionSelector
import com.badoo.ribs.tutorials.tutorial5.rib.option_selector.builder.OptionSelectorBuilder
import com.badoo.ribs.tutorials.tutorial5.util.Lexem
import dagger.Provides
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object GreetingsContainerModule {

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun router(
        // pass component to child rib builders, or remove if there are none
        component: GreetingsContainerComponent,
        savedInstanceState: Bundle?
    ): GreetingsContainerRouter =
        GreetingsContainerRouter(
            savedInstanceState = savedInstanceState,
            helloWorldBuilder = HelloWorldBuilder(component),
            optionSelectorBuilder = OptionSelectorBuilder(component)
        )

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun interactor(
        savedInstanceState: Bundle?,
        router: GreetingsContainerRouter,
        output: Consumer<GreetingsContainer.Output>
    ): GreetingsContainerInteractor =
        GreetingsContainerInteractor(
            savedInstanceState = savedInstanceState,
            router = router,
            output = output,
            options = lexemes()
        )

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun lexemes(): List<@JvmSuppressWildcards Lexem> =
        listOf(
            Lexem.Text("Hello"),
            Lexem.Text("Grüss gott"),
            Lexem.Text("Bonjour"),
            Lexem.Text("Hola"),
            Lexem.Text("Szép jó napot"),
            Lexem.Text("Góðan dag")
        )

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun node(
        savedInstanceState: Bundle?,
        router: GreetingsContainerRouter,
        interactor: GreetingsContainerInteractor
    ) : Node<Nothing> = Node(
        savedInstanceState = savedInstanceState,
        identifier = object : GreetingsContainer {},
        viewFactory = null,
        router = router,
        interactor = interactor
    )

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun helloWorldConfig(
        options: @JvmSuppressWildcards List<Lexem>
    ): HelloWorld.Config =
        HelloWorld.Config(
            welcomeMessage = Lexem.Resource(R.string.hello_world_welcome_text),
            buttonText = options.first()
        )

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun helloWorldOutputConsumer(
        interactor: GreetingsContainerInteractor
    ) : Consumer<HelloWorld.Output> =
        interactor.helloWorldOutputConsumer

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun helloWorldInputSource(
        interactor: GreetingsContainerInteractor
    ) : ObservableSource<HelloWorld.Input> =
        interactor.helloWorldInputSource

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun moreOptionsConfig(
        options: @JvmSuppressWildcards List<Lexem>
    ): OptionSelector.Config =
        OptionSelector.Config(
            options = options
        )

    @GreetingsContainerScope
    @Provides
    @JvmStatic
    internal fun moreOptionsOutput(
        interactor: GreetingsContainerInteractor
    ): Consumer<OptionSelector.Output> =
        interactor.moreOptionsOutputConsumer
}
