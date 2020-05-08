package com.badoo.ribs.core.routing.configuration

import com.badoo.ribs.core.helper.TestRouter.Configuration
import com.badoo.ribs.core.helper.TestRouter.Configuration.C1
import com.badoo.ribs.core.helper.TestRouter.Configuration.C2
import com.badoo.ribs.core.helper.TestRouter.Configuration.C3
import com.badoo.ribs.core.helper.TestRouter.Configuration.C4
import com.badoo.ribs.core.helper.TestRouter.Configuration.C5
import com.badoo.ribs.core.routing.configuration.ConfigurationCommand.Activate
import com.badoo.ribs.core.routing.configuration.ConfigurationCommand.Add
import com.badoo.ribs.core.routing.configuration.ConfigurationCommand.Deactivate
import com.badoo.ribs.core.routing.configuration.ConfigurationCommand.Remove
import com.badoo.ribs.core.routing.configuration.ConfigurationKey.Content
import com.badoo.ribs.core.routing.configuration.ConfigurationKey.Overlay
import com.badoo.ribs.core.routing.configuration.feature.RoutingElement
import org.junit.Assert.assertEquals
import org.junit.Test

class ConnectorCommandCreatorTest {

    @Test
    fun `Content -- () » ()`() {
        val oldStack = backStack()
        val newStack = backStack()
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = emptyList<ConfigurationCommand<Configuration>>()
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1) » (C1)`() {
        val oldStack = backStack(C1)
        val newStack = backStack(C1)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = emptyList<ConfigurationCommand<Configuration>>()
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- () » (C1)`() {
        val oldStack = backStack()
        val newStack = backStack(C1)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Add(Content(0, C1 as Configuration)),
            Activate(Content(0, C1 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1) » ()`() {
        val oldStack = backStack(C1)
        val newStack = backStack()
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(0, C1 as Configuration)),
            Remove(Content(0, C1 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1) » (C1, C2)`() {
        val oldStack = backStack(C1)
        val newStack = backStack(C1, C2)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(0, C1 as Configuration)),
            Add(Content(1, C2 as Configuration)),
            Activate(Content(1, C2 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2) » ()`() {
        val oldStack = backStack(C1, C2)
        val newStack = backStack()
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(1, C2 as Configuration)),
            Remove(Content(1, C2 as Configuration)),
            Remove(Content(0, C1 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2) » (C1)`() {
        val oldStack = backStack(C1, C2)
        val newStack = backStack(C1)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(1, C2 as Configuration)),
            Remove(Content(1, C2 as Configuration)),
            Activate(Content(0, C1 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2) » (C2)`() {
        val oldStack = backStack(C1, C2)
        val newStack = backStack(C2)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(1, C2 as Configuration)),
            Remove(Content(1, C2 as Configuration)),
            Remove(Content(0, C1 as Configuration)),
            Add(Content(0, C2 as Configuration)),
            Activate(Content(0, C2 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2) » (C1, C2)`() {
        val oldStack = backStack(C1, C2)
        val newStack = backStack(C1, C2)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = emptyList<ConfigurationCommand<Configuration>>()
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2) » (C1, C3)`() {
        val oldStack = backStack(C1, C2)
        val newStack = backStack(C1, C3)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(1, C2 as Configuration)),
            Remove(Content(1, C2 as Configuration)),
            Add(Content(1, C3 as Configuration)),
            Activate(Content(1, C3 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2) » (C1, C2, C3)`() {
        val oldStack = backStack(C1, C2)
        val newStack = backStack(C1, C2, C3)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(1, C2 as Configuration)),
            Add(Content(2, C3 as Configuration)),
            Activate(Content(2, C3 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2, C3) » (C1, C2)`() {
        val oldStack = backStack(C1, C2, C3)
        val newStack = backStack(C1, C2)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(2, C3 as Configuration)),
            Remove(Content(2, C3 as Configuration)),
            Activate(Content(1, C2 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2, C3) » (C1)`() {
        val oldStack = backStack(C1, C2, C3)
        val newStack = backStack(C1)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(2, C3 as Configuration)),
            Remove(Content(2, C3 as Configuration)),
            Remove(Content(1, C2 as Configuration)),
            Activate(Content(0, C1 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2, C3) » ()`() {
        val oldStack = backStack(C1, C2, C3)
        val newStack = backStack()
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(2, C3 as Configuration)),
            Remove(Content(2, C3 as Configuration)),
            Remove(Content(1, C2 as Configuration)),
            Remove(Content(0, C1 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2, C3) » (C5)`() {
        val oldStack = backStack(C1, C2, C3)
        val newStack = backStack(C5)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(2, C3 as Configuration)),
            Remove(Content(2, C3 as Configuration)),
            Remove(Content(1, C2 as Configuration)),
            Remove(Content(0, C1 as Configuration)),
            Add(Content(0, C5 as Configuration)),
            Activate(Content(0, C5 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2, C3) » (C1, C2, C4)`() {
        val oldStack = backStack(C1, C2, C3)
        val newStack = backStack(C1, C2, C4)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(2, C3 as Configuration)),
            Remove(Content(2, C3 as Configuration)),
            Add(Content(2, C4 as Configuration)),
            Activate(Content(2, C4 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Content -- (C1, C2, C3) » (C1, C4, C5)`() {
        val oldStack = backStack(C1, C2, C3)
        val newStack = backStack(C1, C4, C5)
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Content(2, C3 as Configuration)),
            Remove(Content(2, C3 as Configuration)),
            Remove(Content(1, C2 as Configuration)),
            Add(Content(1, C4 as Configuration)),
            Add(Content(2, C5 as Configuration)),
            Activate(Content(2, C5 as Configuration))
        )
        assertEquals(expected, actual)
    }


    @Test
    fun `Overlays -- (C1, C2, C3) » (C1, C2, C3 {O1}) -- Add single overlay on last element with no overlays`() {
        val oldStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf()
        )
        val newStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf(Configuration.O1)
        )
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Add(
                Overlay(Overlay.Key(Content(2, C3 as Configuration), 0, Configuration.O1))
            ),
            Activate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 0, Configuration.O1)))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Overlays -- (C1, C2, C3) » (C1, C2, C3 {O1, O2}) -- Add a second overlay on last element with a single overlay`() {
        val oldStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf()
        )
        val newStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf(Configuration.O1, Configuration.O2)
        )
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Add(
                Overlay(Overlay.Key(Content(2, C3 as Configuration), 0, Configuration.O1))
            ),
            Activate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 0, Configuration.O1))),
            Add(
                Overlay(Overlay.Key(Content(2, C3 as Configuration), 1 ,Configuration.O2))
            ),
            Activate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 1 ,Configuration.O2)))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Overlays -- (C1, C2, C3 {O1}) » (C1, C2, C3 {O1, O2}) -- Add multiple overlays on last element with no overlays`() {
        val oldStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf(Configuration.O1)
        )
        val newStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf(Configuration.O1, Configuration.O2)
        )
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Add(
                Overlay(Overlay.Key(Content(2, C3 as Configuration), 1, Configuration.O2))
            ),
            Activate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 1, Configuration.O2)))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Overlays -- (C1, C2, C3 {O1, O2}) » (C1, C2, C3 {O1}) -- Remove single overlay on last element with multiple overlays`() {
        val oldStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf(Configuration.O1, Configuration.O2)
        )
        val newStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf(Configuration.O1)
        )
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 1, Configuration.O2))),
            Remove(Overlay(Overlay.Key(Content(2, C3 as Configuration), 1, Configuration.O2)))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Overlays -- (C1, C2, C3 {O1, O2}) » (C1, C2, C3) -- Remove all overlays on last element with multiple overlays`() {
        val oldStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf(Configuration.O1, Configuration.O2)
        )
        val newStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf()
        )
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 1, Configuration.O2))),
            Remove(Overlay(Overlay.Key(Content(2, C3 as Configuration), 1, Configuration.O2))),
            Deactivate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 0, Configuration.O1))),
            Remove(Overlay(Overlay.Key(Content(2, C3 as Configuration), 0, Configuration.O1)))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Overlays -- (C1, C2, C3 {O1, O2}) » (C1, C2) -- Remove last back stack element with multiple overlays`() {
        val oldStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(),
            C3 to listOf(Configuration.O1, Configuration.O2)
        )
        val newStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf()
        )
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 1, Configuration.O2))),
            Deactivate(Overlay(Overlay.Key(Content(2, C3 as Configuration), 0, Configuration.O1))),
            Deactivate(Content(2, C3 as Configuration)),
            Remove(Overlay(Overlay.Key(Content(2, C3 as Configuration), 1, Configuration.O2))),
            Remove(Overlay(Overlay.Key(Content(2, C3 as Configuration), 0, Configuration.O1))),
            Remove(Content(2, C3 as Configuration)),
            Activate(Content(1, C2 as Configuration))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Overlays -- (C1, C2 {O1, O2}, C3) » (C1, C2 {O1, O2}) -- Going back to previous back stack element with multiple overlays`() {
        val oldStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(Configuration.O1, Configuration.O2),
            C3 to listOf()
        )
        val newStack = backStackWithOverlays(
            C1 to listOf(),
            C2 to listOf(Configuration.O1, Configuration.O2)
        )
        val actual = ConfigurationCommandCreator.diff(oldStack, newStack)
        val expected = listOf<ConfigurationCommand<Configuration>>(
            Deactivate<Configuration>(Content(2, C3 as Configuration)),
            Remove<Configuration>(Content(2, C3 as Configuration)),
            Activate<Configuration>(Content(1, C2 as Configuration)),
            Activate<Configuration>(Overlay(Overlay.Key(Content(1, C2 as Configuration), 0, Configuration.O1))),
            Activate<Configuration>(Overlay(Overlay.Key(Content(1, C2 as Configuration), 1, Configuration.O2)))
        )
        assertEquals(expected, actual)
    }

    private fun backStack(vararg configurations: Configuration): List<RoutingElement<Configuration>> =
        configurations.map { RoutingElement(it) }

    private fun backStackWithOverlays(vararg configurations: Pair<Configuration, List<Configuration>>): List<RoutingElement<Configuration>> =
        configurations.map { RoutingElement(it.first, it.second) }
}
