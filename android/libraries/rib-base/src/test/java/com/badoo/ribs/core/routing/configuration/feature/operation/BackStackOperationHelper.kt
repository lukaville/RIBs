package com.badoo.ribs.core.routing.configuration.feature.operation

import com.badoo.ribs.core.helper.TestRouter
import com.badoo.ribs.core.routing.configuration.feature.RoutingElement

fun List<TestRouter.Configuration>.asBackStackElements(): BackStack<TestRouter.Configuration> =
    this.map {
        RoutingElement(it)
    }

