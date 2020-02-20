package com.badoo.ribs.core.routing.configuration.feature

import android.os.Parcelable
import com.badoo.ribs.core.routing.configuration.ConfigurationContext
import com.badoo.ribs.core.routing.configuration.ConfigurationContext.ActivationState
import com.badoo.ribs.core.routing.configuration.ConfigurationContext.ActivationState.ACTIVE
import com.badoo.ribs.core.routing.configuration.ConfigurationContext.ActivationState.SLEEPING
import com.badoo.ribs.core.routing.configuration.ConfigurationKey
import kotlinx.android.parcel.Parcelize

/**
 * The state of [ConfigurationFeature] in a form that can be persisted to [android.os.Bundle].
 */
@Parcelize
internal data class SavedState<C : Parcelable>(
    val pool: Map<ConfigurationKey, ConfigurationContext.Unresolved<C>>
) : Parcelable {

    /**
     * Converts the [SavedState] to [WorkingState]
     */
    fun toWorkingState(): WorkingState<C> =
        WorkingState(
            SLEEPING,
            pool
        )
}

/**
 * The state [ConfigurationFeature] can work with.
 *
 * @param activationLevel represents the maximum level any [ConfigurationContext] can be activated to. Can be either [SLEEPING] or [ACTIVE].
 * @param pool            represents the pool of all [ConfigurationContext] elements
 */
internal data class WorkingState<C : Parcelable>(
    val activationLevel: ActivationState = SLEEPING,
    val pool: Map<ConfigurationKey, ConfigurationContext<C>> = mapOf(),
    val ongoingTransitions: List<OngoingTransition<C>> = emptyList()
) {
    /**
     * Converts the [WorkingState] to [SavedState] by shrinking all
     * [ConfigurationContext.Resolved] elements back to [ConfigurationContext.Unresolved]
     */
    fun toSavedState(): SavedState<C> =
        SavedState(
            pool.map {
                it.key to when (val entry = it.value) {
                    is ConfigurationContext.Unresolved -> entry
                    is ConfigurationContext.Resolved -> entry.shrink()
                }.copy(
                    activationState = it.value.activationState.sleep()
                )
            }.toMap()
        )

    val hasOngoingTransition: Boolean
        get() = ongoingTransitions.isNotEmpty()
}

internal fun <C : Parcelable> WorkingState<C>.withDefaults(
    defaults: Map<ConfigurationKey, ConfigurationContext<C>>
) =
    copy(
        // Defaults should not overwrite existing elements
        pool = pool + defaults + pool
    )
