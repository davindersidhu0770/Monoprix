package com.production.monoprix

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.production.monoprix.util.SessionManager



class StickyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val sessionManager = SessionManager(this)
        if(!sessionManager.stayLoggedIn) {
            val settings = getSharedPreferences("Monoprix", MODE_PRIVATE)
            settings.edit().clear().apply()
        }
    }

}