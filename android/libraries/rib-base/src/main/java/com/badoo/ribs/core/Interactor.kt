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
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import com.badoo.ribs.core.view.RibView
import io.reactivex.disposables.Disposable
import java.util.UUID

/**
 * The base implementation for all [Interactor]s.
 *
 * @param <C> the type of Configuration this Interactor can expect to push to its [Router].
 * @param <V> the type of [RibView].
 **/
abstract class Interactor<V : RibView>(
    savedInstanceState: Bundle?,
    private val disposables: Disposable?
) : Plugin<V>, Identifiable {

    private val savedInstanceState = savedInstanceState?.getBundle(BUNDLE_KEY)

    internal var tag = this.savedInstanceState?.getString(KEY_TAG) ?: "${this::class.java.name}.${UUID.randomUUID()}"
        private set

    override val id: String
        get() = tag

    override fun onAttach(nodeLifecycle: Lifecycle) {
        onAttach(nodeLifecycle, savedInstanceState)
    }

    protected open fun onAttach(nodeLifecycle: Lifecycle, savedInstanceState: Bundle?) {
        // TODO remove this method
    }

    override fun onDetach() {
        disposables?.dispose()
    }

    internal fun onViewCreated(viewLifecycle: Lifecycle, view: V) {
        onViewCreated(view, viewLifecycle)
    }

    override fun onViewCreated(view: V, viewLifecycle: Lifecycle) {
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        val bundle = Bundle()
        bundle.putString(KEY_TAG, tag)
        outState.putBundle(BUNDLE_KEY, bundle)
    }

    companion object {
        internal const val BUNDLE_KEY = "Interactor"
        internal const val KEY_TAG = "interactor.tag"
    }
}
