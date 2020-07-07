package com.badoo.ribs.routing.source.tabcontroller

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.element.TimeCapsule
import com.badoo.mvicore.extension.mapNotNull
import com.badoo.mvicore.feature.ReducerFeature
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.history.RoutingHistory
import com.badoo.ribs.routing.history.RoutingHistoryElement
import com.badoo.ribs.routing.history.RoutingHistoryElement.Activation.ACTIVE
import com.badoo.ribs.routing.source.RoutingSource
import com.badoo.ribs.routing.source.tabcontroller.operation.Drop
import com.badoo.ribs.routing.source.tabcontroller.operation.Init
import com.badoo.ribs.routing.source.tabcontroller.operation.Operation
import com.badoo.ribs.routing.source.tabcontroller.operation.RemoveById
import com.badoo.ribs.routing.source.tabcontroller.operation.Revert
import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.functions.Consumer

private val timeCapsuleKey = TabController::class.java.name
private fun <C : Parcelable> TimeCapsule<State<C>>.initialState(): State<C> =
    (get(timeCapsuleKey) ?: State())

/**
 * State store implementing [RoutingSource] that keeps a simple linear history of [Routing]s.
 *
 * It will maintain a [RoutingHistory] such the last, and only the last [RoutingHistoryElement] is
 * set to active [RoutingHistoryElement.Activation.ACTIVE].
 *
 * Elements are persisted to Bundle (see [AndroidTimeCapsule]) and restored automatically.
 *
 * @see TabController.Operation for supported operations
 * @see TabController.BootstrapperImpl for operations emitted during initialisation
 * @see TabController.ActorImpl for logic deciding whether an operation should be carried out
 * @see TabController.ReducerImpl for the implementation of applying state changes
 */
class TabController<C : Parcelable> internal constructor(
    private val configurations: Collection<C>,
    private val active: C,
    private val timeCapsule: AndroidTimeCapsule
)  : Consumer<Operation<C>>, RoutingSource<C> {

    private val feature = ReducerFeature<Operation<C>, State<C>, Nothing>(
        initialState = timeCapsule.initialState(),
        bootstrapper = BootstrapperImpl(
            timeCapsule.initialState(),
            configurations,
            active
        ),
        reducer = ReducerImpl(
            contentIdFactory = this::contentIdForConfiguration
        )
    )

    val state: State<C>
        get() = feature.state

    val activeConfiguration: ObservableSource<C>
        get() = Observable.wrap(feature)
            .mapNotNull {
                it.current
                    .find { it.activation == ACTIVE }
                    ?.routing
                    ?.configuration
            }

    override fun baseLineState(fromRestored: Boolean): RoutingHistory<C>  =
        timeCapsule.initialState()

    constructor(
        configurations: Collection<C>,
        active: C,
        buildParams: BuildParams<*>
    ) : this(
        configurations,
        active,
        AndroidTimeCapsule(buildParams.savedInstanceState)
    )

    init {
        timeCapsule.register(timeCapsuleKey) { feature.state }
    }

    /**
     * The set of back stack operations affecting the state.
     */
    sealed class Effect<C : Parcelable> {
        data class Applied<C : Parcelable>(
            val operation: Operation<C>
        ) : Effect<C>()
    }

    /**
     * Automatically sets [initialConfiguration] as [NewRoot] when initialising the [TabController]
     */
    class BootstrapperImpl<C : Parcelable>(
        private val state: State<C>,
        private val configurations: Collection<C>,
        private val active: C
    ) : Bootstrapper<Operation<C>> {
        override fun invoke(): Observable<Operation<C>> =
            when {
            state.current.isEmpty() -> just(
                Init(
                    configurations,
                    active
                )
            )
            else -> empty()
        }
    }

    /**
     * Creates a new [State] based on the old one + the applied [Effect]
     */
    @SuppressWarnings("LongMethod")
    class ReducerImpl<C : Parcelable>(
        val contentIdFactory: (C) -> Routing.Identifier
    ) : Reducer<State<C>, Operation<C>> {
        override fun invoke(state: State<C>, effect: Operation<C>): State<C> {
            if (effect.isApplicable(state)) {
                Log.d("TabController", "Effect is applicable: $effect")
                val applied = effect.invoke(state)
                val withCorrectIds = applied.current
                    .map { element ->
                        element.copy(
                            routing = element.routing.copy(
                                identifier = contentIdFactory(element.routing.configuration)
                            )
                        )
                    }.toSet()

                val newState = applied.copy(
                    current = withCorrectIds
                )

                Log.d("TabController", "old state: $state")
                Log.d("TabController", "new state: $newState")

                return newState
            }

            return state
        }
    }

    override fun handleBackPressFallback(): Boolean {
        if (Revert<C>().isApplicable(state)) {
            accept(Revert())
//            accept(Drop())
            return true
        }

        return false
    }

    override fun onTransitionFinished() {
        Log.d("TabController", "onTransitionFinished")
        accept(Drop())
    }

    override fun remove(identifier: Routing.Identifier) {
        accept(
            RemoveById(
                identifier
            )
        )
    }

    override fun accept(operation: Operation<C>) {
        feature.accept(operation)
    }

    override fun subscribe(observer: Observer<in RoutingHistory<C>>) =
        Observable.wrap(feature)
            .map { RoutingHistory.from(it.current) }
            .subscribe(observer)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timeCapsule.saveState(outState)
    }

    internal fun contentIdForConfiguration(configuration: C): Routing.Identifier =
        Routing.Identifier("Tab controller ${System.identityHashCode(this)} #$configuration")
}
