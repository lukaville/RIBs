package com.badoo.ribs.example.rib.switcher

import com.badoo.ribs.core.plugin.Router
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature
import com.badoo.ribs.example.rib.switcher.subtree.Configuration
import com.badoo.ribs.example.rib.switcher.subtree.Configuration.Content
import com.badoo.ribs.example.rib.switcher.subtree.Configuration.Permanent

typealias SwitcherBackStack = BackStackFeature<Configuration, SwitcherView>

//typealias SwitcherSubtree = Router<Configuration, Permanent, Content, SwitcherView>
