package com.badoo.ribs.routing.source.tabcontroller

import android.os.Parcelable
import com.badoo.ribs.routing.history.RoutingHistory
import com.badoo.ribs.routing.history.RoutingHistoryElement
import kotlinx.android.parcel.Parcelize

typealias Snapshot<C> = Set<RoutingHistoryElement<C>> // Just RoutingHistory would do fine, solve parcelize on it

@Parcelize
data class State<C : Parcelable>(
    val history: List<Snapshot<C>> = emptyList(),
    val current: Snapshot<C> = emptySet()
) : Parcelable, RoutingHistory<C> {

    override fun iterator(): Iterator<RoutingHistoryElement<C>> =
        current.iterator()
}


