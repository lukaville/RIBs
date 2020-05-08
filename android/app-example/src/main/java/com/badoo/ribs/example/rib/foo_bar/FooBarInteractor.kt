package com.badoo.ribs.example.rib.foo_bar

import android.Manifest
import com.badoo.ribs.core.builder.BuildParams
import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.mvicore.binder.using
import com.badoo.ribs.android.PermissionRequester
import com.badoo.ribs.android.PermissionRequester.RequestPermissionsEvent
import com.badoo.ribs.android.PermissionRequester.RequestPermissionsEvent.Cancelled
import com.badoo.ribs.android.PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult
import com.badoo.ribs.core.Interactor
import com.badoo.ribs.example.rib.foo_bar.FooBarView.Event.CheckPermissionsButtonClicked
import com.badoo.ribs.example.rib.foo_bar.FooBarView.Event.RequestPermissionsButtonClicked
import com.badoo.ribs.example.rib.foo_bar.FooBarView.ViewModel
import com.badoo.ribs.example.rib.foo_bar.analytics.FooBarAnalytics
import com.badoo.ribs.example.rib.foo_bar.mapper.ViewEventToAnalyticsEvent
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.functions.Consumer

class FooBarInteractor(
    buildParams: BuildParams<Nothing?>,
    private val permissionRequester: PermissionRequester
) : Interactor<FooBarView>(
    buildParams = buildParams
) {

    companion object {
        private const val REQUEST_CODE_CAMERA = 1
    }

    private val dummyViewInput = PublishRelay.create<FooBarView.ViewModel>()

    override fun onViewCreated(view: FooBarView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(view to FooBarAnalytics using ViewEventToAnalyticsEvent)
            bind(view to viewEventConsumer)
            bind(dummyViewInput to view)
            bind(permissionRequester.events(this@FooBarInteractor) to permissionEventConsumer)
        }

        dummyViewInput.accept(
            ViewModel("My id: " + id.replace("${FooBarInteractor::class.java.name}.", ""))
        )
    }

    private val viewEventConsumer: Consumer<FooBarView.Event> = Consumer {
        when (it) {
            CheckPermissionsButtonClicked -> checkPermissions()
            RequestPermissionsButtonClicked -> requestPermissions()
        }
    }

    private fun checkPermissions() {
        val result = permissionRequester.checkPermissions(
            client = this,
            permissions = arrayOf(
                Manifest.permission.CAMERA
            )
        )

        dummyViewInput.accept(ViewModel(result.toString()))
    }

    private fun requestPermissions() {
        permissionRequester.requestPermissions(
            client = this,
            requestCode = REQUEST_CODE_CAMERA,
            permissions = arrayOf(
                Manifest.permission.CAMERA
            )
        )
    }

    private val permissionEventConsumer: Consumer<RequestPermissionsEvent> = Consumer {
        // If it's a single request code, we might as well ignore the whole branching
        // as it's impossible to receive events that are not meant for this RIB
        when (it.requestCode) {
            REQUEST_CODE_CAMERA -> when (it) {
                // We can also filter for only actual results in the stream so even this branching is not necessary
                is RequestPermissionsResult -> dummyViewInput.accept(ViewModel("Permission event: $it"))
                is Cancelled -> dummyViewInput.accept(ViewModel("Permission request cancelled"))
            }

        }
    }
}
