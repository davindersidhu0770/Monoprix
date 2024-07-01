package com.production.monoprix.util


import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.text.BidiFormatter
import android.text.TextDirectionHeuristics
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.production.monoprix.R
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.StatusModel
import com.production.monoprix.model.StatusTokenModel
import java.io.File
import java.lang.ref.WeakReference
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class Utils {
    companion object {
        lateinit var pd: Dialog
        var DEVICE_TYPE = "2"
        var iaDrawerOpen: Boolean = false
        var gLogin: Boolean = false
        var fLogin: Boolean = false
        var isContactUs: Boolean = false
        var duration: String = "00:10"
        var mid: String = "MONOPRIX2020"
        val nameValidation = "^[\\p{L} .'-]+$"
        lateinit var session: SessionManager
        var uniqueID: String? = null
        val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"
        lateinit var mViewReference: WeakReference<Context>

        fun isAtLeastVersion(version: Int): Boolean {
            return Build.VERSION.SDK_INT >= version
        }

        fun showProgressDialogBanner(context: Context, isShow: Boolean) {
            if (!(context as Activity).isFinishing) {
                pd = Dialog(context)
                pd.setContentView(R.layout.dialog_progressbar)
                pd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                pd.setCanceledOnTouchOutside(false)
                if (pd.isShowing) {
                    pd.dismiss()
                }
                if (isShow) {
                    pd.show()
                }
            } else {
                if (pd.isShowing) {
                    pd.dismiss()
                }
            }
            /* val handler = Handler()
             handler.postDelayed(Runnable { pd.dismiss() }, 10000)*/
        }

        fun isAppInForeground(context: Context): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager.runningAppProcesses
            if (appProcesses != null) {
                for (appProcess in appProcesses) {
                    if (appProcess.processName == context.packageName && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true
                    }
                }
            }
            return false
        }

        fun numbersWithoutLocalization(number: Double): String {
            return java.lang.String.format(
                Locale("en", "US"),
                "%.2f",
                number
            )
        }

        fun numbersIntWithoutLocalization(number: Int): String {
            return java.lang.String.format(
                Locale("en", "US"),
                "%d",
                number
            )
        }

        fun isNetworkAvailable(context: Context?): Boolean {
            if (context == null) return false
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            return true
                        }
                    }
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            }
            return false
        }

        fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }

        fun toSimpleString(date: Date, desiredFormat: String): String {
            val format = SimpleDateFormat(desiredFormat, Locale.ENGLISH)
            return format.format(date)
        }

        fun convertStringDateToAnotherStringDate(
            stringdate: String?,
            stringdateformat: String?,
            returndateformat: String?
        ): String? {
            return try {
                val date = SimpleDateFormat(stringdateformat, Locale.ENGLISH).parse(stringdate)
                SimpleDateFormat(returndateformat, Locale.ENGLISH).format(date)
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                ""
            }
        }

        fun getCurrentDateTime(): Date {
            return Calendar.getInstance().time
        }

        fun openFile(context: Context, file: File) {
            try {
                val URI = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext().getPackageName()
                        .toString() + ".provider",
                    file
                )

//                val uri: Uri = Uri.fromFile(file)
                /*val uri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName.toString() + ".provider",
                    url
                )*/

                val intent = Intent(Intent.ACTION_VIEW)
                if (file.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(URI, "application/pdf")
                } else {
                    intent.setDataAndType(URI, "*/*")
                }
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context!!.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context!!,
                    "No application found which can open the file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        fun timeIsGreater(time: String, endtime: String): Boolean {
            val pattern = "hh:mm aa"
            Log.e("==== ", "$time ==== $endtime")
            val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
            try {
                val date1 = sdf.parse(time)
                val date2 = sdf.parse(endtime)
                return date1.after(date2)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return false
        }

        fun stringWithoutLocalization(number: String): String {

            return BidiFormatter.getInstance(Locale("en")).unicodeWrap(
                number,
                TextDirectionHeuristics.LTR
            )
        }

/*
        fun showProgressDialog(context: Context, isShow: Boolean) {
            if (!(context as Activity).isFinishing) {
                pd = Dialog(context)
                pd.setContentView(R.layout.dialog_progressbar)
                pd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                pd.setCanceledOnTouchOutside(false)
                if (pd.isShowing) {
                    pd.dismiss()
                }
                if (isShow) {
                    pd.show()
                }
            } else {
                if (pd.isShowing) {
                    pd.dismiss()
                }
            }


        }
*/

        fun showProgressDialog(context: Context, isShow: Boolean) {
            if (!(context as Activity).isFinishing) {
                // Initialize pd before using it
                pd = Dialog(context)
                pd.setContentView(R.layout.dialog_progressbar)
                pd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                pd.setCanceledOnTouchOutside(false)
                if (pd.isShowing) {
                    pd.dismiss()
                }
                if (isShow) {
                    pd.show()
                }
            } else {
                if (pd.isShowing) {
                    pd.dismiss()
                }
            }
        }


        /*token*/
        fun token(context: Context) {
            mViewReference = WeakReference(context)
            getSalt()
        }

        fun getSalt() {
            ApiManager.getSalt()
                .subscribe(
                    this::handleSaltResponse, this::handleError
                )
        }

        private fun handleSaltResponse(response: StatusModel) {
            if (response.status == 200) {
                hmac(response.data.key, response.data.key + mid)
                session.maxotptry = response.data.maxOTPTry
                session.otpmaxmsg = response.data.otpMaxMsg
            } else {
                Toast.makeText(mViewReference.get(), response.statusMessage, Toast.LENGTH_SHORT)
                    .show()

            }
        }

        private fun hmac(salt: String, text: String) {
            try {
                val secret = salt
                val message = text
                val sha256_HMAC = Mac.getInstance("HmacSHA256")
                val secret_key = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
                sha256_HMAC.init(secret_key)
                val hash = android.util.Base64.encodeToString(
                    sha256_HMAC.doFinal(message.toByteArray()),
                    android.util.Base64.NO_WRAP
                )
                val encodedItem = URLEncoder.encode(hash, "utf-8")
                getToken(encodedItem)
                println(hash)
            } catch (e: Exception) {
                println("Error")
            }

        }

        fun getToken(hash: String) {
            ApiManager.getToken(hash)
                .subscribe(
                    this::handleResponse, this::handleError
                )
        }

        private fun handleResponse(rsponse: StatusTokenModel) {
            session = SessionManager(mViewReference.get()!!)
            if (rsponse.status == 200) {
                val c = Calendar.getInstance()
                System.out.println("Current time =&gt; " + c.time)
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val formattedDate = df.format(c.time)
                session.tokencreationtime = formattedDate.toString()
                session.token = rsponse.data
            }
        }

        private fun handleError(error: Throwable) {
            Toast.makeText(
                mViewReference.get(),
                "Error ${error.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }

        fun tokenExpire(context: Context) {
            mViewReference = WeakReference(context)
            session = SessionManager(mViewReference.get()!!)
            if (session.tokencreationtime!!.isNotEmpty()) {
                val currentTime = Calendar.getInstance().time
                val spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val oldDate = spf.parse(session.tokencreationtime)
                val mills = currentTime.time - oldDate!!.time
                val hours = mills / (1000 * 60 * 60)
                val mins = (mills / (1000 * 60) % 60).toInt()
                val diff = "$hours:$mins"
                if (diff > duration) {
                    token(mViewReference.get()!!)
                }
            } else {
                token(mViewReference.get()!!)
            }
        }

        fun getDeviceId(context: Context): String? {
            if (uniqueID == null) {
                val sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE
                )
                uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)

                Log.d("8Dec:", "uniqueID : "+uniqueID)

                if (uniqueID == null) {

                    Log.d("8Dec:", "uniqueID == null : ")


                    //uniqueID = UUID.randomUUID().toString();
                    uniqueID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

                    Log.d("8Dec:", "uniqueID after: "+uniqueID)

/*
                    Toast.makeText(
                        context,
                        uniqueID,
                        Toast.LENGTH_LONG
                    ).show()
*/

//                    Log.d("8Dec:", "UUID after: "+UUID.randomUUID().toString())

                    val editor = sharedPrefs.edit()
                    editor.putString(PREF_UNIQUE_ID, uniqueID)
                    editor.commit()
                }
            }
            return uniqueID
        }


    }


}