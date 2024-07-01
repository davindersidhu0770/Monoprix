package com.production.monoprix

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.ui.home.HomeFragment
import com.production.monoprix.util.ApplicationLifecycleHandler
import com.production.monoprix.util.ForceUpdateChecker
import com.production.monoprix.util.PermissionCallback
import com.production.monoprix.util.SessionManager
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import io.reactivex.annotations.NonNull
import java.lang.ref.WeakReference
import java.util.*


class MyApplication : Application(), PermissionCallback {

    private lateinit var session: SessionManager

    override fun onCreate() {
        super.onCreate()
        appContext = WeakReference(applicationContext)

        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)

        session = SessionManager(this)
        if (session.languageselect == "مع") {
            setLocale("ar")
        } else {
            setLocale("en")

        }
        val config = TwitterConfig.Builder(this)
            .twitterAuthConfig(
                TwitterAuthConfig(
                    resources.getString(R.string.twitter_api_key),
                    resources.getString(R.string.twitter_secret_key)
                )
            )
            .debug(true) // Set to false in production
            .build()
        Twitter.initialize(config)

        val handler = ApplicationLifecycleHandler(this)
        registerActivityLifecycleCallbacks(handler)
        registerComponentCallbacks(handler)

        // set in-app defaults firebase updates
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val remoteConfigDefaults: MutableMap<String, Any> = HashMap()
        remoteConfigDefaults[ForceUpdateChecker.KEY_FORCE_REQUIRED] = false
        remoteConfigDefaults[ForceUpdateChecker.KEY_SOFT_REQUIRED] = false
        remoteConfigDefaults[ForceUpdateChecker.KEY_CURRENT_VERSION] = "1.0"

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults)

        firebaseRemoteConfig.fetch(60)
            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                override fun onComplete(@NonNull task: Task<Void?>) {
                    if (task.isSuccessful()) {
                        Log.d("2Jan: ", "Remote config is fetched.")
                        firebaseRemoteConfig.fetchAndActivate()

                    } else {
                        Log.d("2Jan: ", "Fetch failed.")

                        // Use default values after 30 seconds if fetch fails.
                    }
                }
            })

    }

    override fun attachBaseContext(base: Context?) {
        localeManager = LocaleManager(base)
        super.attachBaseContext(localeManager!!.setLocale(base!!))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager!!.setLocale(this)
    }

    companion object {
        lateinit var appContext: WeakReference<Context>
        var localeManager: LocaleManager? = null
    }


    fun setLocale(s: String) {
        val activityRes = resources
        val activityConf = activityRes.configuration
        val newLocale = Locale(s)
        activityConf.setLocale(newLocale)
        activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)
        val applicationRes = applicationContext.resources
        val applicationConf = applicationRes.configuration
        applicationConf.setLocale(newLocale)
        applicationRes.updateConfiguration(
            applicationConf, applicationRes.displayMetrics
        )
    }

    override fun onPermissionGranted() {
        Log.d("16Jan", "onPermissionGranted")

    }

    override fun onPermissionDenied() {
        Log.d("16Jan", "onPermissionDenied")

    }

    override fun showExplanationDialog() {
        Log.d("16Jan", "showExplanationDialog App")

    }

    override fun showSettingsDialog() {
        Log.d("16Jan", "showSettingsDialog")

    }

}