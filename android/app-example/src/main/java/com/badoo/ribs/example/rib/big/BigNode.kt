package com.badoo.ribs.example.rib.big

import com.badoo.ribs.core.builder.BuildParams
import android.view.ViewGroup
import com.badoo.ribs.core.Node

class BigNode(
    buildParams: BuildParams<*>,
    viewFactory: ((ViewGroup) -> BigView?)?,
    private val router: BigRouter,
    private val interactor: BigInteractor
) : Node<BigView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    router = router,
    interactor = interactor
), Big {
    override val node: Node<BigView> = this
}
