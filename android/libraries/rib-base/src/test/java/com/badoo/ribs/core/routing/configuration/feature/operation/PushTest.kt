package com.badoo.ribs.core.routing.configuration.feature.operation

import com.badoo.ribs.core.helper.TestRouter.Configuration
import com.badoo.ribs.core.helper.TestRouter.Configuration.C1
import com.badoo.ribs.core.helper.TestRouter.Configuration.C2
import com.badoo.ribs.core.routing.configuration.feature.RoutingElement
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PushTest {

    private lateinit var push: Push<Configuration>

    @Test
    fun `not applicable when current element with same configuration`() {
        val backStack = listOf(C1).asBackStackElements()
        push = Push(C1)

        val applicable = push.isApplicable(backStack)

        assertThat(applicable).isEqualTo(false)
    }

    @Test
    fun `not applicable when current element same with different overlays`() {
        val backStack = listOf(
            RoutingElement(C1, listOf(C2))
        )
        push = Push(C1)

        val applicable = push.isApplicable(backStack)

        assertThat(applicable).isEqualTo(false)
    }

    @Test
    fun `applicable when current element with different configuration`() {
        val backStack = listOf(C1, C2).asBackStackElements()
        push = Push(C1)

        val applicable = push.isApplicable(backStack)

        assertThat(applicable).isEqualTo(true)
    }

    @Test
    fun `invoke add configuration when push`() {
        val backStack = listOf(C1).asBackStackElements()
        push = Push(C2)

        val newBackStack = push.invoke(backStack)

        assertThat(newBackStack).containsExactly(
            RoutingElement(C1),
            RoutingElement(C2)
        )
    }
}
