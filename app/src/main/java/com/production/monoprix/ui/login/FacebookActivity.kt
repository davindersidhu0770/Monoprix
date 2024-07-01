package com.production.monoprix.ui.login


import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.production.monoprix.R
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_facebook.*
import org.json.JSONException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class FacebookActivity : AppCompatActivity() {

    private var mFacebookCallbackManager: CallbackManager? = null
    lateinit var id: String
    lateinit var firstName: String
    lateinit var lastName: String
    var fEmail: String? = ""
    lateinit var name: String
//  lateinit var gender: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook)
        init()
    }

    fun init() {

        LoginManager.getInstance().logOut()
        /*hash key for facebook login*/
        hashKey()
/*
        FacebookSdk.sdkInitialize(this);
*/
        mFacebookCallbackManager = CallbackManager.Factory.create()
        btnLoginFacebook.setReadPermissions(
            "email",
            "public_profile"
        )

/*
        LoginManager.getInstance().logInWithReadPermissions(
            this,
            (Arrays.asList("public_profile", "email"))
        )
*/

        arrayOf<String?>("email", "public_profile")
        btnLoginFacebook.performClick()

        btnLoginFacebook.loginBehavior = LoginBehavior.WEB_ONLY

        btnLoginFacebook.registerCallback(
            mFacebookCallbackManager!!,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val accessToken = loginResult.accessToken
                    val profile = Profile.getCurrentProfile()
                    val token = AccessToken.getCurrentAccessToken()
                    Log.d("access only Token is", token?.token.toString())
                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, response ->
                        Log.v("LoginActivity Response ", response.toString())
                        try {
                            if (`object` != null) {
                                id = `object`.getString("id")
                            }
                            if (`object` != null) {
                                name = `object`.getString("name")
                            }
                            if (`object` != null) {
                                firstName = `object`.getString("first_name")
                            }
                            if (`object` != null) {
                                lastName = `object`.getString("last_name")
                            }
                            if (`object` != null) {
                                fEmail = `object`.getString("email")
                            }
                            /* if (`object` != null) {
                                 gender = `object`.getString("gender")
                             }*/
                            val intent = Intent()
                            intent.putExtra("id", id)
                            intent.putExtra("fname", firstName)
                            intent.putExtra("lname", lastName)
                            intent.putExtra("fEmail", fEmail)
                            intent.putExtra("accessToken", token?.token.toString())
                            setResult(300, intent)
                            finish()
                        } catch (e: JSONException) {
                            val intent = Intent()
                            intent.putExtra("id", id)
                            intent.putExtra("fname", firstName)
                            intent.putExtra("lname", lastName)
                            intent.putExtra("fEmail", fEmail)
                            intent.putExtra("accessToken", token?.token.toString())
                            setResult(300, intent)
                            finish()
                        }

                    }
                    val parameters = Bundle()
                    parameters.putString(
                        "fields",
                        "id,name,first_name,last_name,email,gender, birthday"
                    )
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    finish()
                }

                override fun onError(exception: FacebookException) {
                    exception.localizedMessage?.let { Log.e("27March: ", it) }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Utils.fLogin = true
        mFacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
        Log.d("3april>>>", "onActivityResult")

    }

    private fun hashKey() {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
            //getting application package name, as defined in manifest
            val packageName = this.getApplicationContext().getPackageName()

            //Retriving package info
            packageInfo = this.getPackageManager().getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )

            Log.e("Package Name=", this.getApplicationContext().getPackageName())

            for (signature in packageInfo.signatures) {

                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))
                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}