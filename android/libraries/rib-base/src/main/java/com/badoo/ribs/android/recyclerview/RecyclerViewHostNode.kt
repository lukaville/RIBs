package com.badoo.ribs.android.recyclerview

import android.os.Bundle
import android.os.Parcelable
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.RibView

class RecyclerViewHostNode<T : Parcelable> internal constructor(
    savedInstanceState: Bundle? = null,
    router: RecyclerViewHostRouter<T>,
    private val viewDeps: RecyclerViewHostView.Dependency,
    interactor: RecyclerViewHostInteractor<T>,
    private val timeCapsule: AndroidTimeCapsule,
    private val adapter: Adapter<T>
) : Node<RibView>(
    identifier = object : RecyclerViewHost {},
    savedInstanceState = savedInstanceState,
    viewFactory = { RecyclerViewHostViewImpl.Factory().invoke(viewDeps).invoke(it) },
    plugins = listOf(interactor, router)
) {
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timeCapsule.saveState(outState)
    }

    override fun onDetach() {
        adapter.onDestroy()
        super.onDetach()
    }
}
