package com.badoo.ribs.core.routing.configuration

import com.badoo.mvicore.element.TimeCapsule
import com.badoo.ribs.core.helper.TestRouter.Configuration
import com.badoo.ribs.core.helper.TestRouter.Configuration.*
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature
import com.badoo.ribs.core.routing.configuration.feature.BackStackElement
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeatureState
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature.Operation.NewRoot
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature.Operation.Pop
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature.Operation.Push
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature.Operation.PushOverlay
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature.Operation.Replace
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class BackStackFeatureTest {

    companion object {
        private val initialConfiguration = C1
    }

    private lateinit var timeCapsuleEmpty: TimeCapsule<BackStackFeatureState<Configuration>>
    private lateinit var timeCapsuleWithContent: TimeCapsule<BackStackFeatureState<Configuration>>
    private lateinit var backstackInTimeCapsule: List<BackStackElement<Configuration>>
    private lateinit var backStackManager: BackStackFeature<Configuration>

    @Before
    fun setUp() {
        backstackInTimeCapsule = listOf<BackStackElement<Configuration>>(
            BackStackElement(C3),
            BackStackElement(C2)
        )

        timeCapsuleEmpty = mock()
        timeCapsuleWithContent = mock {
            on { get<BackStackFeatureState<Configuration>>(BackStackFeature::class.java.name) } doReturn BackStackFeatureState(
                backstackInTimeCapsule
            )
        }
        setupBackStackManager(timeCapsuleEmpty)
    }

    private fun setupBackStackManager(timeCapsule: TimeCapsule<BackStackFeatureState<Configuration>>) {
        backStackManager = BackStackFeature(
            initialConfiguration,
            timeCapsule
        )
    }

    @Test
    fun `Initial back stack contains only one element`() {
        assertThat(backStackManager.state.currentBackStack, hasSize(1))
    }

    @Test
    fun `Initial state matches initial configuration`() {
        assertEquals(initialConfiguration, backStackManager.state.current!!.configuration)
    }

    @Test
    fun `After state restoration back stack matches the one in the time capsule`() {
        setupBackStackManager(timeCapsuleWithContent)
        assertEquals(backstackInTimeCapsule, backStackManager.state.currentBackStack)
    }

    @Test
    fun `Back stack state's current() references last item`() {
        setupBackStackManager(timeCapsuleWithContent)
        assertEquals(backstackInTimeCapsule.last(), backStackManager.state.current)
    }

    @Test
    fun `Wish_Push once results in the back stack size growing by one`() {
        backStackManager.accept(Push(C4))
        assertEquals(2, backStackManager.state.currentBackStack.size)
    }

    @Test
    fun `Wish_Push once adds the expected new element to the end of the back stack`() {
        backStackManager.accept(Push(C4))
        assertEquals(C4, backStackManager.state.current!!.configuration)
    }

    @Test
    fun `Wish_Push same elements multiple times has no effect after first`() {
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C2))
        val expected = listOf(
            initialConfiguration,
            C2
        )
        assertEquals(expected, backStackManager.state.currentBackStack.map { it.configuration })
    }

    @Test
    fun `Wish_Push multiple different elements results in expected backstack content`() {
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C3))
        backStackManager.accept(Push(C4))
        backStackManager.accept(Push(C5))
        val expected = listOf(
            initialConfiguration,
            C2,
            C3,
            C4,
            C5
        )
        assertEquals(expected, backStackManager.state.currentBackStack.map { it.configuration })
    }

    @Test
    fun `Wish_PushOverlay does not grow the back stack size`() {
        backStackManager.accept(PushOverlay(O1))
        assertEquals(1, backStackManager.state.currentBackStack.size)
    }

    @Test
    fun `Wish_PushOverlay adds a new element to the overlay list of the last back stack element`() {
        backStackManager.accept(PushOverlay(O1))
        assertEquals(1, backStackManager.state.currentBackStack.last().overlays.size)
    }

    @Test
    fun `Wish_PushOverlay adds the expected configuration to the overlay list of the last back stack element`() {
        backStackManager.accept(PushOverlay(O1))
        assertEquals(O1, backStackManager.state.currentBackStack.last().overlays.last())
    }

    @Test
    fun `Wish_PushOverlay multiple times with same elements has no effect after the first`() {
        backStackManager.accept(PushOverlay(O1))
        backStackManager.accept(PushOverlay(O1))
        backStackManager.accept(PushOverlay(O1))
        assertEquals(listOf(O1), backStackManager.state.currentBackStack.last().overlays)
    }

    @Test
    fun `Wish_PushOverlay multiple times with different elements adds all new ones to the overlay list of the last back stack element`() {
        backStackManager.accept(PushOverlay(O1))
        backStackManager.accept(PushOverlay(O2))
        assertEquals(2, backStackManager.state.currentBackStack.last().overlays.size)
    }

    @Test
    fun `Wish_PushOverlay multiple times adds the expected elements to the overlay list of the last back stack element`() {
        backStackManager.accept(PushOverlay(O1))
        backStackManager.accept(PushOverlay(O2))
        val expected = listOf(
            O1,
            O2
        )
        assertEquals(expected, backStackManager.state.currentBackStack.last().overlays)
    }

    @Test
    fun `Wish_Replace does not change back stack size`() {
        // initial size: 1
        backStackManager.accept(Push(C2)) // should increase to 2
        backStackManager.accept(Push(C3)) // should increase to 3
        backStackManager.accept(Replace(C4)) // should keep 3
        assertEquals(3, backStackManager.state.currentBackStack.size)
    }

    @Test
    fun `Wish_Replace puts the correct configuration at the end of the back stack`() {
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C3))
        backStackManager.accept(Replace(C4))
        assertEquals(C4, backStackManager.state.current!!.configuration)
    }

    @Test
    fun `Wish_Replace same configuration as current has no effect`() {
        backStackManager.accept(Push(C2))
        backStackManager.accept(PushOverlay(O1))
        val beforeReplace = backStackManager.state
        backStackManager.accept(Replace(C2))
        assertEquals(beforeReplace, backStackManager.state)
    }

    @Test
    fun `Wish_Replace consecutively results in expected backstack content`() {
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C3))
        backStackManager.accept(Replace(C4))
        backStackManager.accept(Replace(C5))
        val expected = listOf(
            initialConfiguration,
            C2,
            C5
        )
        assertEquals(expected, backStackManager.state.currentBackStack.map { it.configuration })
    }

    @Test
    fun `Wish_NewRoot results in new back stack with only one element`() {
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C3))
        backStackManager.accept(NewRoot(C4))
        assertEquals(1, backStackManager.state.currentBackStack.size)
    }

    @Test
    fun `Wish_NewRoot puts the correct configuration at the end of the back stack`() {
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C3))
        backStackManager.accept(NewRoot(C4))
        assertEquals(C4, backStackManager.state.current!!.configuration)
    }

    @Test
    fun `Wish_NewRoot consecutively results in expected backstack content`() {
        backStackManager.accept(Push(C2))
        backStackManager.accept(Push(C3))
        backStackManager.accept(NewRoot(C4))
        backStackManager.accept(NewRoot(C5))
        val expected = listOf(
            C5
        )
        assertEquals(expected, backStackManager.state.currentBackStack.map { it.configuration })
    }

    @Test
    fun `Wish_NewRoot same configuration as current has no effect`() {
        backStackManager.accept(NewRoot(C2))
        backStackManager.accept(PushOverlay(O1))
        val beforeReplace = backStackManager.state
        backStackManager.accept(NewRoot(C2))
        assertEquals(beforeReplace, backStackManager.state)
    }

    @Test
    fun `Wish_Pop does not change back stack if there's only one entry`() {
        val lastElementBeforePop = backStackManager.state.current
        backStackManager.accept(Pop())
        assertEquals(lastElementBeforePop, backStackManager.state.current)
    }

    @Test
    fun `Wish_Pop reduces size of back stack if there's more than one entry`() {
        // initial size: 1
        backStackManager.accept(Push(C2)) // should increase size to: 2
        backStackManager.accept(Push(C3)) // should increase size to: 3
        backStackManager.accept(Push(C4)) // should increase size to: 4
        backStackManager.accept(Pop())
        assertEquals(3, backStackManager.state.currentBackStack.size)
    }

    @Test
    fun `Wish_Pop results in expected new back stack`() {
        // initial size: 1
        backStackManager.accept(Push(C2)) // should increase size to: 2
        backStackManager.accept(Push(C3)) // should increase size to: 3
        backStackManager.accept(Push(C4)) // should increase size to: 4
        backStackManager.accept(Pop())
        val expected = listOf(
            initialConfiguration,
            C2,
            C3
        )
        assertEquals(expected, backStackManager.state.currentBackStack.map { it.configuration })
    }

    @Test
    fun `Wish_Pop doesn't pop content if there are overlays`() {
        // initial size: 1
        backStackManager.accept(Push(C2)) // should increase size to: 2
        backStackManager.accept(Push(C3)) // should increase size to: 3
        backStackManager.accept(Push(C4)) // should increase size to: 4
        backStackManager.accept(PushOverlay(O1)) // should keep size at: 4 + 1 overlay
        backStackManager.accept(PushOverlay(O2)) // should keep size at: 4 + 2 overlays
        backStackManager.accept(Pop()) // should keep size at: 4 + 1 overlay

        assertEquals(4, backStackManager.state.currentBackStack.size)
    }

    @Test
    fun `Wish_Pop keeps expected configurations if there are overlays`() {
        // initial size: 1
        backStackManager.accept(Push(C2)) // should increase size to: 2
        backStackManager.accept(Push(C3)) // should increase size to: 3
        backStackManager.accept(Push(C4)) // should increase size to: 4
        backStackManager.accept(PushOverlay(O1)) // should keep size at: 4 + 1 overlay
        backStackManager.accept(PushOverlay(O2)) // should keep size at: 4 + 2 overlays
        backStackManager.accept(Pop()) // should keep size at: 4 + 1 overlay

        val expected = listOf(
            initialConfiguration,
            C2,
            C3,
            C4
        )

        assertEquals(expected, backStackManager.state.currentBackStack.map { it.configuration })
    }

    @Test
    fun `Wish_Pop results in popping Overlay if there's at least one on the current element`() {
        backStackManager.accept(PushOverlay(O1))
        backStackManager.accept(PushOverlay(O2))
        backStackManager.accept(PushOverlay(O3))
        backStackManager.accept(Pop())

        assertEquals(2, backStackManager.state.currentBackStack.last().overlays.size)
    }

    @Test
    fun `Wish_Pop results in expected Overlays`() {
        backStackManager.accept(PushOverlay(O1))
        backStackManager.accept(PushOverlay(O2))
        backStackManager.accept(PushOverlay(O3))
        backStackManager.accept(Pop())

        val expectedOverlays = listOf(
            O1,
            O2
        )

        assertEquals(expectedOverlays, backStackManager.state.currentBackStack.last().overlays)
    }
}
