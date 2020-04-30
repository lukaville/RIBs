package com.badoo.ribs.example.rib.hello_world

import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.ConceptView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.customisation.inflate
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.hello_world.HelloWorldView.Event
import com.badoo.ribs.example.rib.hello_world.HelloWorldView.ViewModel
import com.badoo.ribs.example.rib.small.SmallNode
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface HelloWorldView : ConceptView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        object ButtonClicked : Event()
    }

    data class ViewModel(
        val text: String
    )

    interface Factory : ViewFactory<Nothing?, HelloWorldView>
}

class HelloWorldViewImpl private constructor(
    override val androidView: ViewGroup,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : HelloWorldView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_hello_world
    ) : HelloWorldView.Factory {
        override fun invoke(deps: Nothing?): (ViewGroup) -> HelloWorldView = {
            HelloWorldViewImpl(
                inflate(it, layoutRes)
            )
        }
    }

    private val text: TextView = androidView.findViewById(R.id.hello_debug)
    private val launchButton: TextView = androidView.findViewById(R.id.hello_button_launch)
    private val smallContainer: ViewGroup = androidView.findViewById(R.id.small_container)

    init {
        launchButton.setOnClickListener { events.accept(Event.ButtonClicked) }
    }

    override fun accept(vm: ViewModel) {
        text.text = vm.text
    }

    override fun getParentViewForChild(child: Node<*>): ViewGroup? =
        when (child) {
            is SmallNode -> smallContainer
            else -> null
        }
}
