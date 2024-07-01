package com.production.monoprix.util

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.production.monoprix.R

interface PermissionCallback {
    fun onPermissionGranted()
    fun onPermissionDenied()
    fun showExplanationDialog()
    fun showSettingsDialog()
}

class ApplicationLifecycleHandler(private val permissionCallback: PermissionCallback) :
    Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private var isInBackground = false
    private var currentActivity: Activity? = null
    private val POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE = 22
    private lateinit var layoutInflater: LayoutInflater

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        // Initialize layoutInflater here when an activity is created
        layoutInflater = LayoutInflater.from(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity

        if (isInBackground) {
            Log.d("16Jan", "App came to foreground")
            isInBackground = false

            // Ensure that the activity is in a valid state before showing the dialog
            if (!activity.isFinishing && !activity.isDestroyed) {
                checkAndRequestNotificationPermission()
            }
        }
    }

    fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (currentActivity?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                requestPostNotificationsPermission()
            } else {
                Log.d("10Jan", "ELSE")
            }
        }
    }

    private fun requestPostNotificationsPermission() {
        if (currentActivity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } == true
        ) {
            showExplanationDialog()
            Log.d("10Jan", "showExplanationDialog")
        } else {
            Log.d("10Jan", "else requestcode")
            currentActivity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun showExplanationDialog() {
        currentActivity?.let { activity ->
            if (activity is FragmentActivity) {
                val dialogFragment = ExplanationDialogFragment.newInstance()
                dialogFragment.show(activity.supportFragmentManager, "ExplanationDialogFragment")
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onConfigurationChanged(configuration: android.content.res.Configuration) {}
    override fun onLowMemory() {}
    override fun onTrimMemory(i: Int) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.d("16Jan", "App went to background")
            isInBackground = true
        }
    }
}
