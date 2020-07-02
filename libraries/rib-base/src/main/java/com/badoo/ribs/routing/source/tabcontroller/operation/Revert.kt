package com.badoo.ribs.routing.source.tabcontroller.operation

import android.os.Parcelable
import com.badoo.ribs.routing.source.tabcontroller.State

class Revert<C : Parcelable> :
    Operation<C> {

    override fun isApplicable(state: State<C>): Boolean =
        state.previous != null

    override fun invoke(state: State<C>): State<C> =
        state.copy(
            previous = null,
            current = state.previous!!
        )

}
