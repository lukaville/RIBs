package com.badoo.ribs.example.rib.util

import android.view.ViewGroup
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.Interactor
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.view.RibView
import com.nhaarman.mockitokotlin2.mock

class TestNode<V : RibView>(
    buildParams: BuildParams<*> = BuildParams.Empty(),
    router: Router<*, *, *, *, V> = mock(),
    viewFactory: ((ViewGroup) -> V?)? = mock(),
    interactor: Interactor<V> = mock()
) : Node<V>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = listOf(interactor, router)
) {

    var isAttached: Boolean = false
        private set

    override fun onAttach() {
        super.onAttach()
        isAttached = true
    }

    override fun onDetach() {
        super.onDetach()
        isAttached = false
    }
}
