package com.production.monoprix

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
import android.os.Build.VERSION_CODES.N
import android.os.LocaleList
import androidx.annotation.RequiresApi
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import java.util.*
import kotlin.collections.LinkedHashSet


class LocaleManager(val context: Context?) {
    val LANGUAGE_ENGLISH = "en"
    val LANGUAGE_ARABIC = "ar"

//    private var prefs: SharedPreferences? = null
    var sessionManager: SessionManager=SessionManager(context!!)
    /*fun LocaleManager(context: Context?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }*/

    fun setLocale(c: Context): Context? {
        return updateResources(c, getLanguage())
    }

    fun setNewLocale(c: Context, language: String): Context? {
        persistLanguage(language)
        return updateResources(c, language)
    }

    fun getLanguage(): String? {
        return sessionManager.selectLocale
    }

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(language: String) {
        sessionManager.selectLocale=language
    }

    private fun updateResources(context: Context, language: String?): Context? {
        var context: Context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val config = Configuration(res.configuration)
        if (Utils.isAtLeastVersion(N)) {
            setLocaleForApi24(config, locale)
            context = context.createConfigurationContext(config)
        } else if (Utils.isAtLeastVersion(JELLY_BEAN_MR1)) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    @RequiresApi(api = N)
    private fun setLocaleForApi24(config: Configuration, target: Locale) {
        val set: MutableSet<Locale> = LinkedHashSet()
        // bring the target locale to the front of the list
        set.add(target)
        val all = LocaleList.getDefault()
        for (i in 0 until all.size()) {
            // append other locales supported by the user
            set.add(all[i])
        }
        val locales: Array<Locale> = set.toTypedArray()
        config.setLocales(LocaleList(*locales))
    }

    fun getLocale(res: Resources): Locale? {
        val config: Configuration = res.configuration
        return if (Utils.isAtLeastVersion(N)) config.locales.get(0) else config.locale
    }
}