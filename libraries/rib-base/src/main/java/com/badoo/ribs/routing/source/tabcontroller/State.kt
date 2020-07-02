package com.badoo.ribs.routing.source.tabcontroller

import android.os.Parcelable
import com.badoo.ribs.routing.history.RoutingHistory
import com.badoo.ribs.routing.history.RoutingHistoryElement
import kotlinx.android.parcel.Parcelize

typealias Snapshot<C> = Set<RoutingHistoryElement<C>>

@Parcelize
data class State<C : Parcelable>(
    val previous: Snapshot<C>? = null,
    val current: Snapshot<C> = emptySet()
) : Parcelable, RoutingHistory<C> {

    override fun iterator(): Iterator<RoutingHistoryElement<C>> =
        current.iterator()
}


