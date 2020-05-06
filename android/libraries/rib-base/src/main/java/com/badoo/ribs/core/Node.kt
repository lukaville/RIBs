/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.badoo.ribs.core

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.badoo.ribs.core.Rib.Identifier
import com.badoo.ribs.core.builder.BuildContext
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.exception.RootNodeAttachedAsChildException
import com.badoo.ribs.core.routing.configuration.ConfigurationResolver
import com.badoo.ribs.core.routing.portal.AncestryInfo
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.util.RIBs
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
//import com.uber.rib.util.RibRefWatcher
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Responsible for handling the addition and removal of child nodes.
 **/
@SuppressWarnings("LargeClass")
open class Node<V : RibView>(
    buildParams: BuildParams<*>,
    private val viewFactory: ((ViewGroup) -> V?)?,
    pluginFactory: PluginFactory<V> = { emptyList() }
) : Rib, LifecycleOwner {
    private val plugins: List<Plugin<V>> = pluginFactory.invoke(this)

    companion object {
        internal const val BUNDLE_KEY = "Node"
        internal const val KEY_VIEW_STATE = "view.state"
    }

    override val node: Node<*>
        get() = this

    open val identifier: Rib.Identifier =
        buildParams.identifier

    internal val buildContext: BuildContext =
        buildParams.buildContext

    /**
     * TODO PortalRouter.Configuration.Portal can then work directly with a @Parcelize AncestryInfo,
     *  which was not possible until now.
     */
    internal val ancestryInfo: AncestryInfo =
        buildContext.ancestryInfo

    internal open val attachMode: AttachMode =
        buildContext.attachMode

    // TODO implement plugin.ofType<T> and move this to PortalRouter where it's used
    val resolver: ConfigurationResolver<*>? = plugins.filterIsInstance<ConfigurationResolver<*>>().firstOrNull()
//    val resolver: ConfigurationResolver<out Parcelable>? = router
    private val savedInstanceState = buildParams.savedInstanceState?.getBundle(BUNDLE_KEY)
    internal val externalLifecycleRegistry = LifecycleRegistry(this)
    internal val ribLifecycleRegistry = LifecycleRegistry(this)
    internal var viewLifecycleRegistry: LifecycleRegistry? = null
    val detachSignal = BehaviorRelay.create<Unit>()

    val tag: String = this::class.java.name
    val children = CopyOnWriteArrayList<Node<*>>()
    private val childrenAttachesRelay: PublishRelay<Node<*>> = PublishRelay.create()
    val childrenAttaches: Observable<Node<*>> = childrenAttachesRelay.hide()

    internal open val lifecycleManager = LifecycleManager(this)

    internal val isViewless: Boolean =
        viewFactory == null

    internal open var view: V? = null
    internal var parentViewGroup: ViewGroup? = null

    internal open var savedViewState: SparseArray<Parcelable> =
        savedInstanceState?.getSparseParcelableArray<Parcelable>(KEY_VIEW_STATE) ?: SparseArray()

    internal var isAttachedToView: Boolean = false
        private set

    private var isPendingViewDetach: Boolean = false
    private var isPendingDetach: Boolean = false

    fun getChildren(): List<Node<*>> =
        children.toList()

    init {
        plugins.forEach { it.init(this) }
    }

    @CallSuper
    open fun onAttach() {
        lifecycleManager.onCreateRib()
        plugins.forEach { it.onAttach(lifecycleManager.ribLifecycle.lifecycle) }
    }

    fun attachToView(parentViewGroup: ViewGroup) {
        detachFromView()
        this.parentViewGroup = parentViewGroup
        isAttachedToView = true

        if (!isViewless) {
            createView(parentViewGroup)?.let {
                parentViewGroup.attach(it)
            }
        }

        lifecycleManager.onCreateView()
        view?.let { view ->
            plugins.forEach { it.onViewCreated(view, lifecycleManager.viewLifecycle!!.lifecycle) }
        }
        plugins.forEach { it.onAttachToView(parentViewGroup) }
    }

    private fun createView(parentViewGroup: ViewGroup): V? {
        if (view == null) {
            view = viewFactory?.invoke(parentViewGroup)
        }

        return view
    }

    private fun ViewGroup.attach(view: V) {
        addView(view.androidView)
        view.androidView.restoreHierarchyState(savedViewState)
    }

    internal fun createChildView(child: Node<*>) {
        if (isAttachedToView) {
            child.createView(
                // parentViewGroup is guaranteed to be non-null if and only if view is attached
                (view?.getParentViewForChild(child) ?: parentViewGroup!!)
            )
        }
    }

    fun detachFromView() {
        if (isAttachedToView) {
            plugins.forEach { it.onDetachFromView(parentViewGroup!!) }
            lifecycleManager.onDestroyView()

            if (!isViewless) {
                parentViewGroup!!.removeView(view!!.androidView)
            }

            view = null
            isAttachedToView = false
            this.parentViewGroup = null
            isPendingViewDetach = false
        }
    }

    open fun onDetach() {
        if (isAttachedToView) {
            RIBs.errorHandler.handleNonFatalError(
                "View was not detached before node detach!",
                RuntimeException("View was not detached before node detach! RIB: $this")
            )
            detachFromView()
        }

        lifecycleManager.onDestroyRib()
        plugins.forEach { it.onDetach() }

        for (child in children) {
            detachChildNode(child)
        }

        detachSignal.accept(Unit)
        isPendingDetach = false
    }

    /**
     * Attaches a child node to this node.
     *
     * @param child the [Node] to be attached.
     */
    @MainThread
    internal fun attachChildNode(child: Node<*>) {
        verifyNotRoot(child)
        children.add(child)
//        ribRefWatcher.logBreadcrumb(
//            "ATTACHED", child.javaClass.simpleName, this.javaClass.simpleName
//        )

        lifecycleManager.onAttachChild(child)
        child.onAttach()
        childrenAttachesRelay.accept(child)
        plugins.forEach { it.onAttachChildNode(child) }
    }

    private fun verifyNotRoot(child: Node<*>) {
        if (child.ancestryInfo is AncestryInfo.Root) {
            val message = "A node that is attached as a child should not have a root BuildContext."
            RIBs.errorHandler.handleNonFatalError(
                errorMessage = message,
                throwable = RootNodeAttachedAsChildException(message = "$message. RIB: $this")
            )
        }
    }

    // FIXME internal + protected?
    fun attachChildView(child: Node<*>) {
        if (isAttachedToView) {
            val target = targetViewGroupForChild(child)
            child.attachToView(target)
            plugins.forEach { it.onAttachChildView(child) }
        }
    }

    internal fun targetViewGroupForChild(child: Node<*>): ViewGroup {
        return when {
            // parentViewGroup is guaranteed to be non-null if and only if view is attached
            isViewless -> parentViewGroup!!
            else -> view!!.getParentViewForChild(child) ?: parentViewGroup!!
        }
    }

    // FIXME internal + protected?
    fun detachChildView(child: Node<*>) {
        child.detachFromView()
        plugins.forEach { it.onDetachChildView(child) }
    }

    /**
     * Detaches the node from this parent. NOTE: No consumers of
     * this API should ever keep a reference to the detached child, leak canary will enforce
     * that it gets garbage collected.
     *
     * @param childNode the [Node] to be detached.
     */
    @MainThread
    internal fun detachChildNode(child: Node<*>) {
        plugins.forEach { it.onDetachChildNode(child) }
        children.remove(child)

//        ribRefWatcher.watchDeletedObject(child)
//        ribRefWatcher.logBreadcrumb(
//            "DETACHED", child.javaClass.simpleName, this.javaClass.simpleName
//        )

        child.onDetach()
    }

    internal fun markPendingViewDetach(isPendingViewDetach: Boolean) {
        this.isPendingViewDetach = isPendingViewDetach
    }

    internal fun markPendingDetach(isPendingDetach: Boolean) {
        this.isPendingDetach = isPendingDetach
    }

    /**
     * To be called only from the hosting environment (Activity, Fragment, etc.)
     *
     * For internal usage call onStart() with proper inner lifecycle registry directly
     */
    fun onStart() {
        lifecycleManager.onStartExternal()

    }

    /**
     * To be called only from the hosting environment (Activity, Fragment, etc.)
     *
     * For internal usage call onStop() with proper inner lifecycle registry directly
     */
    fun onStop() {
        lifecycleManager.onStopExternal()
    }

    /**
     * To be called only from the hosting environment (Activity, Fragment, etc.)
     *
     * For internal usage call onResume() with proper inner lifecycle registry directly
     */
    fun onResume() {
        lifecycleManager.onResumeExternal()
    }

    /**
     * To be called only from the hosting environment (Activity, Fragment, etc.)
     *
     * For internal usage call onPause() with proper inner lifecycle registry directly
     */
    fun onPause() {
        lifecycleManager.onPauseExternal()
    }

    @CallSuper
    open fun handleBackPress(): Boolean {
//        ribRefWatcher.logBreadcrumb("BACKPRESS", null, null)
        return plugins.any { it.handleBackPressBeforeDownstream() }
            || delegateHandleBackPressToActiveChildren()
            || plugins.any { it.handleBackPressAfterDownstream() }
    }

    private fun delegateHandleBackPressToActiveChildren(): Boolean =
        children
            .filter { it.isAttachedToView && !(it.isPendingDetach || it.isPendingViewDetach ) }
            .any { it.handleBackPress() }

    internal fun saveViewState() {
        view?.let {
            it.androidView.saveHierarchyState(savedViewState)
        }
    }

    open fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(Identifier.KEY_UUID, identifier.uuid)
        plugins.forEach { it.onSaveInstanceState(outState) }
        saveViewState()

        val bundle = Bundle()
        bundle.putSparseParcelableArray(KEY_VIEW_STATE, savedViewState)
        outState.putBundle(BUNDLE_KEY, bundle)
    }

    fun onLowMemory() {
        plugins.forEach { it.onLowMemory() }
    }

    override fun getLifecycle(): Lifecycle =
        lifecycleManager.lifecycle

    override fun toString(): String =
        identifier.toString()

    /**
     * Executes an action and remains on the same hierarchical level
     *
     * @return the current workflow element
     */
    protected inline fun <reified T> executeWorkflow(
        crossinline action: () -> Unit
    ): Single<T> = Single.fromCallable {
            action()
            this as T
        }
        .takeUntil(detachSignal.firstOrError())

    @VisibleForTesting
    internal inline fun <reified T> executeWorkflowInternal(
        crossinline action: () -> Unit
    ) : Single<T> = executeWorkflow(action)

    /**
     * Executes an action and transitions to another workflow element
     *
     * @param action an action that's supposed to result in the attach of a child (e.g. router.push())
     *
     * @return the child as the expected workflow element, or error if expected child was not found
     */
    @SuppressWarnings("LongMethod")
    protected inline fun <reified T> attachWorkflow(
        crossinline action: () -> Unit
    ): Single<T> = Single.fromCallable {
            action()
            val childNodesOfExpectedType = children.filterIsInstance<T>()
            if (childNodesOfExpectedType.isEmpty()) {
                Single.error<T>(
                    IllegalStateException(
                        "Expected child of type [${T::class.java}] was not found after executing action. " +
                            "Check that your action actually results in the expected child. " +
                            "Child count: ${children.size}. " +
                            "Last child is: [${children.lastOrNull()}]. " +
                            "All children: $children"
                    )
                )
            } else {
                Single.just(childNodesOfExpectedType.last())
            }
        }
        .flatMap { it }
        .takeUntil(detachSignal.firstOrError())

    @VisibleForTesting
    internal inline fun <reified T> attachWorkflowInternal(
        crossinline action: () -> Unit
    ) : Single<T> = attachWorkflow(action)

    /**
     * Waits until a certain child is attached and returns it as the expected workflow element, or
     * returns it immediately if it's already available.
     *
     * @return the child as the expected workflow element
     */
    protected inline fun <reified T> waitForChildAttached(): Single<T> =
        Single.fromCallable {
            val childNodesOfExpectedType = children.filterIsInstance<T>()
            if (childNodesOfExpectedType.isEmpty()) {
                childrenAttaches.ofType(T::class.java).firstOrError()
            } else {
                Single.just(childNodesOfExpectedType.last())
            }
        }
        .flatMap { it }
        .takeUntil(detachSignal.firstOrError())

    @VisibleForTesting
    internal inline fun <reified T> waitForChildAttachedInternal() : Single<T> =
        waitForChildAttached()
}
