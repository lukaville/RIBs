package com.badoo.ribs.example.rib.hello_world

import android.os.Bundle
import android.os.Parcelable
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.badoo.common.ribs.RibsRule
import com.badoo.ribs.RibTestActivity
import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.builder.BuildContext.Companion.root
import com.badoo.ribs.core.routing.portal.Portal
import com.badoo.ribs.example.R
import com.badoo.ribs.example.app.AppRibCustomisations
import io.reactivex.Observable.empty
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import org.junit.Rule
import org.junit.Test

class HelloWorldTestDeferred {

    @get:Rule
    val ribsRule = RibsRule()

    private fun buildRib(ribTestActivity: RibTestActivity, savedInstanceState: Bundle?) =
        HelloWorldBuilder(object : HelloWorld.Dependency {
            override fun portal(): Portal.OtherSide = object : Portal.OtherSide {
                override fun showContent(remoteRouter: Router<*, *, *, *, *>, remoteConfiguration: Parcelable) {}
                override fun showOverlay(remoteRouter: Router<*, *, *, *, *>, remoteConfiguration: Parcelable) {}
            }

            override fun helloWorldInput(): ObservableSource<HelloWorld.Input> = empty()
            override fun helloWorldOutput(): Consumer<HelloWorld.Output> = Consumer {}
            override fun activityStarter(): ActivityStarter = ribTestActivity.activityStarter
        }).build(root(savedInstanceState))

    @Test
    fun testTextDisplayed() {
        ribsRule.start { activity, savedInstanceState -> buildRib(activity, savedInstanceState) }

        onView(withId(R.id.hello_title)).check(matches(isDisplayed()))
    }
}
