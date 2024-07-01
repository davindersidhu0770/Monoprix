package com.production.monoprix.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

/**
 * Created by Harsh on 7/31/2017.
 */

@SuppressLint("WrongConstant")
class SessionManager(context: Context) {

    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    public var PROMOTION_LINK = ""

    var deviceId: String?
        get() = pref.getString(DEVICE_ID, "")
        set(deviceId) {
            editor.putString(DEVICE_ID, deviceId)
            editor.commit()
        }

    var userName: String?
        get() = pref.getString(USER_NAME, "")
        set(userName) {
            editor.putString(USER_NAME, userName)
            editor.commit()
        }

    var barcodePath: String?
        get() = pref.getString(BARCODE_PATH, "")
        set(barcodePath) {
            editor.putString(BARCODE_PATH, barcodePath)
            editor.commit()
        }
    var fcmToken: String?
        get() = pref.getString(FCM_TOKEN, "")
        set(fcmToken) {
            editor.putString(FCM_TOKEN, fcmToken)
            editor.commit()
        }
    var userId: String?
        get() = pref.getString(USER_ID, "")
        set(userId) {
            editor.putString(USER_ID, userId)
            editor.commit()
        }

    var freshInstall: String?
        get() = pref.getString(FRESH_INSTALL, "")
        set(userId) {
            editor.putString(FRESH_INSTALL, userId)
            editor.commit()
        }
    var mobileNumber: String?
        get() = pref.getString(MOBILE_NUMBER, "")
        set(mobileNumber) {
            editor.putString(MOBILE_NUMBER, mobileNumber)
            editor.commit()
        }
    var name: String?
        get() = pref.getString(NAME, "")
        set(name) {
            editor.putString(NAME, name)
            editor.commit()
        }

    //EMAIL_ID
    var email: String?
        get() = pref.getString(EMAIL_ID, "")
        set(email) {
            editor.putString(EMAIL_ID, email)
            editor.commit()
        }
    var countryCode: String?
        get() = pref.getString(COUNTRY_CODE, "")
        set(countryCode) {
            editor.putString(COUNTRY_CODE, countryCode)
            editor.commit()
        }
    var countryId: String?
        get() = pref.getString(COUNTRYID, "")
        set(countryId) {
            editor.putString(COUNTRYID, countryId)
            editor.commit()
        }
    var gender: String?
        get() = pref.getString(GENDER, "")
        set(gender) {
            editor.putString(GENDER, gender)
            editor.commit()
        }
    var volume: Int
        get() = pref.getInt(AUDIO_INDEX, 0)
        set(volume) {
            editor.putInt(AUDIO_INDEX, volume)
            editor.commit()
        }

    var barcode: String?
        get() = pref.getString(BARCODE_RESULT, "")
        set(barcode) {
            editor.putString(BARCODE_RESULT, barcode)
            editor.commit()
        }


    var video: String?
        get() = pref.getString(VIDEO, "")
        set(barcode) {
            editor.putString(VIDEO, barcode)
            editor.commit()
        }

/*
    var promotionLink: String?
        get() = pref.getString(PROMOTION_LINK, "")
        set(barcode) {
            editor.putString(PROMOTION_LINK, barcode)
            editor.commit()
        }
*/


    var shopOnline: String?
        get() = pref.getString(SHOP_ONLINE, "")
        set(barcode) {
            editor.putString(SHOP_ONLINE, barcode)
            editor.commit()
        }

    var totalCart: String?
        get() = pref.getString(TOTAL_CART, "")
        set(totalCart) {
            editor.putString(TOTAL_CART, totalCart)
            editor.commit()
        }
    var subTotal: String?
        get() = pref.getString(SUB_TOTAL, "")
        set(subTotal) {
            editor.putString(SUB_TOTAL, subTotal)
            editor.commit()
        }
    var shopId: String?
        get() = pref.getString(SHOP_ID, "")
        set(shopId) {
            editor.putString(SHOP_ID, shopId)
            editor.commit()
        }
    var cartId: String?
        get() = pref.getString(CART_ID, "")
        set(cartId) {
            editor.putString(CART_ID, cartId)
            editor.commit()
        }
    var familyName: String?
        get() = pref.getString(FAMILY_NAME, "")
        set(familyName) {
            editor.putString(FAMILY_NAME, familyName)
            editor.commit()
        }
    var token: String?
        get() = pref.getString(TOKEN, "")
        set(token) {
            editor.putString(TOKEN, token)
            editor.commit()
        }
    var languageselect: String?
        get() = pref.getString(LANGUAGE_SELECT, "en")
        set(languageselect) {
            editor.putString(LANGUAGE_SELECT, languageselect)
            editor.commit()
        }

    var notiAsked: String?
        get() = pref.getString(NOTIFICATIONDIALOGASKED, "no")
        set(notiAsked) {
            editor.putString(NOTIFICATIONDIALOGASKED, notiAsked)
            editor.commit()
        }
    var selectLocale: String?
        get() = pref.getString(LOCALE_SELECTED, "en")
        set(languageselect) {
            editor.putString(LOCALE_SELECTED, languageselect)
            editor.commit()
        }
    var loyaltyId: String?
        get() = pref.getString(LOYALTYID, "")
        set(loyaltyId) {
            editor.putString(LOYALTYID, loyaltyId)
            editor.commit()
        }
    var loyaltyAmount: String?
        get() = pref.getString(LOYALTYAMOUNT, "")
        set(loyaltyAmount) {
            editor.putString(LOYALTYAMOUNT, loyaltyAmount)
            editor.commit()
        }
    var cardno: String?
        get() = pref.getString(CARDNO, "")
        set(cardno) {
            editor.putString(CARDNO, cardno)
            editor.commit()
        }
    var fingerprint: Boolean
        get() = pref.getBoolean(FINGERPRINT, true)
        set(fingerprint) {
            editor.putBoolean(FINGERPRINT, fingerprint)
            editor.commit()
        }
    var password: String?
        get() = pref.getString(PASSWORD, "")
        set(password) {
            editor.putString(PASSWORD, password)
            editor.commit()
        }
    var tokencreationtime: String?
        get() = pref.getString(TOKENCREATIONTIME, "")
        set(tokencreationtime) {
            editor.putString(TOKENCREATIONTIME, tokencreationtime)
            editor.commit()
        }
    var countryName: String?
        get() = pref.getString(COUNTRYNAME, "")
        set(countryName) {
            editor.putString(COUNTRYNAME, countryName)
            editor.commit()
        }
    var stayLoggedIn: Boolean
        get() = pref.getBoolean(STAYLOGGEDIN, true)
        set(stayLoggedIn) {
            editor.putBoolean(STAYLOGGEDIN, stayLoggedIn)
            editor.commit()
        }
    var storeLocation: String?
        get() = pref.getString(STORELOCATION, "")
        set(storeLocation) {
            editor.putString(STORELOCATION, storeLocation)
            editor.commit()
        }
    var instaToken: String?
        get() = pref.getString(INSTA_TOKEN, "")
        set(instaToken) {
            editor.putString(INSTA_TOKEN, instaToken)
            editor.commit()
        }
    var maxotptry: String?
        get() = pref.getString(MAXOTPTRY, "")
        set(maxotptry) {
            editor.putString(MAXOTPTRY, maxotptry)
            editor.commit()
        }
    var otpmaxmsg: String?
        get() = pref.getString(OTPMAXMSG, "")
        set(otpmaxmsg) {
            editor.putString(OTPMAXMSG, otpmaxmsg)
            editor.commit()
        }
    var server: String?
        get() = pref.getString(TYPESERVER, "")
        set(server) {
            editor.putString(TYPESERVER, server)
            editor.commit()
        }
    var storelocationarabic: String?
        get() = pref.getString(STORELOCATIONARABIC, "")
        set(storelocationarabic) {
            editor.putString(STORELOCATIONARABIC, storelocationarabic)
            editor.commit()
        }

    init {
        pref = context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE)
        editor = pref.edit()

    }


    companion object {
        private val PRIVATE_MODE = 0
        private const val PREFERENCE_NAME = "Monoprix"
        private const val DEVICE_ID = "deviceid"
        private const val FCM_TOKEN = "fcmtoken"
        private const val USER_ID = "userid"
        private const val FRESH_INSTALL = "freshinstall"
        private const val MOBILE_NUMBER = "mobilenumber"
        private const val NAME = "name"
        private const val EMAIL_ID = "email_id"
        private const val COUNTRY_CODE = "countrycode"
        private const val GENDER = "gender"
        private const val BARCODE_RESULT = "barcoderesult"
        private const val VIDEO = "video"
        private const val SHOP_ONLINE = "SHOP_ONLINE"
        private const val TOTAL_CART = "totalcart"
        private const val SUB_TOTAL = "subtotal"
        private const val SHOP_ID = "shopid"
        private const val COUNTRYID = "countryid"
        private const val CART_ID = "cartid"
        private const val FAMILY_NAME = "familyname"
        private const val TOKEN = "token"
        private const val LANGUAGE_SELECT = "languageselect"
        private const val NOTIFICATIONDIALOGASKED = "notidialogasked"
        private const val LOYALTYID = "loyltyid"
        private const val LOYALTYAMOUNT = "loyltyamount"
        private const val CARDNO = "cardno"
        private const val PASSWORD = "password"
        private const val FINGERPRINT = "fingerprint"
        private const val TOKENCREATIONTIME = "tokencreationtime"
        private const val COUNTRYNAME = "countryname"
        private const val STAYLOGGEDIN = "stayloggedin"
        private const val STORELOCATION = "storelocation"
        private const val MAXOTPTRY = "maxotptry"
        private const val OTPMAXMSG = "OtpMaxMsg"
        private const val TYPESERVER = "server"
        private const val STORELOCATIONARABIC = "storelocationarabic"
        private const val AUDIO_INDEX = "volume"
        private const val LOCALE_SELECTED = "locale"
        private const val INSTA_TOKEN = "insta_token"
        private const val USER_NAME = "userName"
        private const val BARCODE_PATH = "barcodePath"
    }


}
