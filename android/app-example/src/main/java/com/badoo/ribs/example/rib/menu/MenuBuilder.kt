package com.badoo.ribs.example.rib.menu

import android.os.Bundle
import com.badoo.ribs.core.Builder
import com.badoo.ribs.core.Node
import com.badoo.ribs.customisation.customisationsBranchFor
import com.badoo.ribs.customisation.getOrDefault
import com.badoo.ribs.example.rib.menu.feature.MenuFeature

class MenuBuilder(
    dependency: Menu.Dependency
) : Builder<Menu.Dependency>() {

    override val dependency : Menu.Dependency = object : Menu.Dependency by dependency {
        override fun ribCustomisation() = dependency.customisationsBranchFor(Menu::class)
    }

    fun build(savedInstanceState: Bundle?): Node<MenuView> {
        val customisation = dependency.getOrDefault(Menu.Customisation())
        val feature = MenuFeature()
        val interactor = MenuInteractor(
            savedInstanceState,
            dependency.menuInput(),
            dependency.menuOutput(),
            feature
        )

        return MenuNode(
            savedInstanceState = savedInstanceState,
            viewFactory = customisation.viewFactory(null),
            interactor = interactor
        )
    }
}
