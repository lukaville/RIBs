package com.badoo.ribs.tutorials.tutorial7.rib.hello_world

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import com.jakewharton.rxrelay2.PublishRelay
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.tutorials.tutorial7.R
import com.badoo.ribs.tutorials.tutorial7.rib.hello_world.HelloWorldView.Event
import com.badoo.ribs.tutorials.tutorial7.rib.hello_world.HelloWorldView.ViewModel
import com.badoo.ribs.tutorials.tutorial7.util.Lexem
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface HelloWorldView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        object HelloButtonClicked : Event()
        object MoreOptionsButtonClicked : Event()
    }

    data class ViewModel(
        val titleText: Lexem,
        val welcomeText: Lexem,
        val buttonText: Lexem
    )
}

class HelloWorldViewImpl private constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, private val events: PublishRelay<Event>
) : ConstraintLayout(context, attrs, defStyle),
    HelloWorldView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
    ) : this(context, attrs, defStyle, PublishRelay.create<Event>())

    override val androidView = this
    private val title: TextView by lazy { findViewById<TextView>(R.id.hello_world_title) }
    private val welcome: TextView by lazy { findViewById<TextView>(R.id.hello_world_welcome) }
    private val helloButton: Button by lazy { findViewById<Button>(R.id.hello_world_hello_button) }
    private val moreOptionsButton: Button by lazy { findViewById<Button>(R.id.hello_world_more_options) }

    override fun onFinishInflate() {
        super.onFinishInflate()
        helloButton.setOnClickListener { events.accept(Event.HelloButtonClicked) }
        moreOptionsButton.setOnClickListener { events.accept(Event.MoreOptionsButtonClicked) }
    }

    override fun accept(vm: ViewModel) {
        title.text = vm.titleText.resolve(context)
        welcome.text = vm.welcomeText.resolve(context)
        helloButton.text = vm.buttonText.resolve(context)
    }
}
