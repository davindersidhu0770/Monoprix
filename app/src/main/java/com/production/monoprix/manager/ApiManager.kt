package com.production.monoprix.manager


import com.production.monoprix.MyApplication
import com.production.monoprix.api.ApiService
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.CipherSuite
import java.util.Collections

object ApiManager {

    // private const val SERVER: String = "http://monoprix.atpik.com/api/V1.0/"
    // private const val SERVER: String = " http://78.100.18.87:9003/api/V1.0/"
    // private const val SERVER: String = "http://106.51.72.246:9011/api/V1.0/"
    private const val SERVER: String = "https://s2pmt.alibinali.com:81/api/v1.0/"
    public const val STOREIMAGE: String = "https://s2pmt.alibinali.com:81/images/10002_Musherib.jpg"

    //    public const val MRESTAURANTWEBLINK: String = "https://www.monoprix.qa/monoprix-me/mrestaurants/west-bay/"
//    public const val MRESTAURANTWEBLINK: String = "https://www.talabat.com/qatar/monoprix-al-dafna/"
    public const val MRESTAURANTWEBLINK: String =
        "https://www.monoprix.qa/monoprix-me/mrestaurants/west-bay/"
    public const val FACEBOOKWEBLINK: String = "https://www.facebook.com/MonoprixQatar"
    public const val INSTAGRAMWEBLINK: String = "https://www.instagram.com/monoprixqatar/"
    public const val TWITTERWEBLINK: String = "https://twitter.com/MonoprixQatar/"
    public const val YOUTUBEWEBLINK: String =
        "https://www.youtube.com/channel/UCaM_VMNs97kCVTpYBl358UQ"

    public const val SHOPONLINELINK: String = "https://www.monoprix.qa/shop-online/"

    // private const val SERVER: String = "http://192.168.1.10:9012/api/V1.0/"
    private lateinit var mService: ApiService
    private var client: OkHttpClient.Builder? = null
    private var sessionManager: SessionManager


    init {
        val context = MyApplication.appContext
        sessionManager = SessionManager(context.get()!!.applicationContext)
        if (SERVER == "https://s2pmt.alibinali.com:81/api/v1.0/") {
            sessionManager.server = "QA"
        } else {
            sessionManager.server = ""
        }
        val retrofit = initRetrofit()
        initServices(retrofit)
    }

    private fun initRetrofit(): Retrofit {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
            .supportsTlsExtensions(true)
            .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA
            )
            .build()

        client = OkHttpClient.Builder()
            .connectionSpecs(Collections.singletonList(spec)).apply {
                connectTimeout(1, TimeUnit.MINUTES)
                readTimeout(1, TimeUnit.MINUTES)
                writeTimeout(1, TimeUnit.MINUTES)
                networkInterceptors().add(Interceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .method(original.method(), original.body())
                        .header("Authorization", sessionManager.token.toString())
                        .build()
                    chain.proceed(request)
                })
                addInterceptor(interceptor)
            }

/*
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts:  Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?){}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate>  = arrayOf()
            })

            // Install the all-trusting trust manager
            val  sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() &&  trustAllCerts.first() is X509TrustManager) {
                client!!.sslSocketFactory(sslSocketFactory, trustAllCerts.first() as X509TrustManager)
//                client!!.hostnameVerifier { HostnameVerifier {-> true } }
            }

        } catch (e: Exception) {
        }
*/

        return Retrofit.Builder().baseUrl(SERVER)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(createMoshiConverter())
            .client(client!!.build())
            .build()
    }

    private fun createMoshiConverter(): MoshiConverterFactory = MoshiConverterFactory.create()

    private fun initServices(retrofit: Retrofit) {
        mService = retrofit.create(ApiService::class.java)
    }

    /*login*/
    fun loadLogin(
        email: String,
        password: String,
        device_id: String,
        device_token: String,
        fcmtoken: String,
        deviceId: String,
        isFingerLogin: Boolean
    ) =
        mService.getLogin(
            email,
            password,
            device_id,
            Utils.DEVICE_TYPE,
            device_token,
            fcmtoken,
            isFingerLogin,
            "android",
            deviceId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!


    /*DeviceIDChangeRequestEmail*/
    fun DeviceIDChangeRequestEmail(
        userId: String,
        deviceId: String
    ) =
        mService.DeviceIDChangeRequestEmail(
            userId, deviceId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!


    /*DeviceIDUpdated*/
    fun DeviceIDUpdated(
        userId: String,
        deviceId: String
    ) =
        mService.DeviceIDUpdated(
            userId, deviceId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!


    /*sign up*/
    fun loadSignUp(
        title: String,
        name: String,
        familyname: String,
        email: String,
        countryId: Int,
        mobile: String,
        password: String,
        flagyears: Boolean,
        flagTerms: Boolean,
        deviceId: String,
        dob: String,
        country: String,
        area: String

    ) =
        mService.doSignUP(
            title,
            name,
            familyname,
            email,
            countryId,
            mobile,
            password,
            flagyears,
            flagTerms, "android", deviceId,
            dob, country,
            area
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*verify otp*/
    fun loadVerifyOtp(
        userId: Int,
        otp: String,
        email: String,
        mobile: String,
        isAccUpdate: Boolean
    ) =
        mService.getOtp(userId, otp, email, mobile, isAccUpdate, "android")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*resend otp*/
    fun doResendOtp(mobile_number: Int) =
        mService.doResendOtp(mobile_number, "android")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*forgot password*/
    fun doForgotPassword(email: String, type: String) =
        mService.doForgotPassword(email, type, "android")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*social log in*/
    fun doSocialLogin(
        Title: String,
        Name: String,
        FamilyName: String,
        Email: String,
        CountryId: Int,
        MobileNumber: String,
        Flag18years: Boolean,
        flgTermsOfService: Boolean,
        socialmediaId: String,
        socialmediatoken: String,
        Islogin: Boolean,
        social_type: String,
        fcmtoken: String,
        deviceId: String
    ) = mService.doSocialLogin(
        Title,
        Name,
        FamilyName,
        Email,
        CountryId,
        MobileNumber,
        Flag18years,
        flgTermsOfService,
        socialmediaId,
        socialmediatoken,
        Islogin,
        social_type, "android",
        fcmtoken,
        deviceId
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get all our stores*/
    fun getStores() =
        mService.getStores()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*get store by location*/
    fun getStoreLocation(latitude: String) =
        mService.getStoreLocation(latitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    //AddressList
    fun getShippingAddressList(user_id: String) =
        mService.getShippingAddressList(user_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!


    //Edit Address
    fun getEditShippingAddress(
        shipping_address_id: Int,
        title: String,
        userId: Int,
        areaId: Int,
        addressDesc: String,
        location: String,
        postCode: String,
        mobile: String,
        landmark: String,
        building: String,
        houseNo: String,
        street: String,
        cityId: Int,
        countryId: Int,
        isDefalut: Boolean,
        zoneId: String,
        municipality: String,
        place: String,
        area: String
    ) =
        mService.getEditShippingAddress(
            shipping_address_id,
            title,
            userId,
            areaId,
            addressDesc,
            location,
            postCode,
            mobile,
            landmark, building, houseNo, street,
            cityId,
            countryId,
            isDefalut, zoneId,
            municipality,
            place,
            area
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    //Add New Address
    fun addNewAddressDetails(
        title: String,
        userId: Int,
        areaId: Int,
        addressDesc: String,
        location: String,
        postCode: String,
        mobile: String,
        landmark: String,
        building: String,
        houseNo: String,
        street: String,
        cityId: Int,
        countryId: Int,
        isDefalut: Boolean,
        zoneId: String,
        municipality: String,
        place: String,
        area: String
    ) =
        mService.addNewAddressDetails(
            title,
            userId,
            areaId,
            addressDesc,
            location,
            postCode,
            mobile,
            landmark,
            building,
            houseNo,
            street,
            cityId,
            countryId,
            isDefalut,
            zoneId,
            municipality, place,
            area
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!


    //Get City List
    fun getCityList(countryId: Int) =
        mService.getCityList(countryId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    //Get Area List
    fun getAreaList(city_id: Int) =
        mService.getAreaList(city_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    //get CMS
    fun getCMSDetails(cms_id: String) =
        mService.getCMS()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    //termsandconditons
    fun getCMSTermsAndConditions(cms_id: String) =
        mService.getCMSTermsAndConditions()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    //privacy policy
    fun getPandP() =
        mService.getPrivacyPolicy()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    fun getFaqLink() =
        mService.getFaq()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    //help
    fun getHelp() =
        mService.getHelp()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*add product*/
    fun addProduct(jsonArray: JSONArray) = mService.doAddProduct(jsonArray)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*RedeemLoyalty*/
    fun RedeemLoyalty(
        userId: String,
        total: String,
        loyaltybalance: String,
        amountToRedeem: String
    ) = mService.RedeemLoyalty(userId, total, loyaltybalance, amountToRedeem)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get product list*/
    fun getProductList(user_id: String, cartId: Int) =
        mService.getProductList(user_id, cartId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*update cart*/
    fun doUpdateCart(cart_item_id: String, quantity: String) =
        mService.getCartUpdate(cart_item_id, quantity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*dlete cart*/
    fun doDeleteCart(cart_item_id: String) =
        mService.doCartDelete(cart_item_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*clear cart*/
    fun doClearCart(cartId: String) =
        mService.doCartClear(cartId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*product detail*/
    fun getProductDetail(shopId: String, code: String, location: String, userid: String) =
        mService.getProductDetails(shopId, code, location, userid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*time slot*/
    fun getTimeSlot() = mService.getTimeSlot().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!


    /*payment*/
    fun doPayment(
        cartId: String,
        userId: String,
        paymentmode: String,
        isdeliveryopted: Boolean,
        addressId: Int,
        deliveryslotId: Int,
        language: String,
        loyaltyRedeemed: String
    ) =
        mService.doPayment(
            cartId,
            userId,
            paymentmode,
            isdeliveryopted,
            addressId,
            deliveryslotId,
            language,
            loyaltyRedeemed
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*countries*/
    fun getCountries() = mService.getCountries()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    fun getZones() = mService.getZones()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*update account*/
    fun doUpdateAccount(
        userId: Int, title: String, name: String, familyName: String, email: String, countryId: Int,
        country: String,
        mobile_number: String, password: String, loyaltynumber: String, area: String, dob: String
    ) = mService.doUpdateAccount(
        userId, title, name, familyName, email,
        countryId, country, mobile_number, password, "android", loyaltynumber, area, dob
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*change password*/
    fun doChangePassword(
        userId: Int,
        oldpassword: String,
        newpassword: String,
        platform: String,
        email: String
    ) =
        mService.doChangePassword(
            userId, oldpassword, newpassword, platform, email
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*order history*/
    fun getOrderHistoryList(userId: Int) = mService.getOrderHistoryList(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*order details*/
    fun getOrderDetails(id: Int) = mService.getOrderDetails(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*contact us*/
    fun getContactUs() = mService.getContactUs()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*submit contact us*/
    fun doContactUs(name: String, email: String, mobile: String, message: String) =
        mService.doContactUs(name, email, mobile, message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*get banners*/
    fun getBanner() = mService.getBanner()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get PromotionOnline*/  //Added by Jaideep on 12-jan-2022.
    fun getPromotionOnline() = mService.getPromotionOnline()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    fun getBenefits() = mService.getBenefit()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get banners*/
    fun getPromotionBanner() = mService.getPromotionBanner()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get loylity*/
    fun getLoyality(userId: Int) = mService.getLoylity(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get account details*/
    fun getAccountDetails(userId: Int) = mService.getAccountDetails(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    fun delAddress(id: String, userId: String) = userId.let {
        mService.delAddressById(id, it)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }!!

    fun delAccountById(userId: String) = mService.delAccount(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get promotions*/
    fun getPromotions() = mService.getPromotion()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get token*/
    fun getToken(key: String) = mService.getToken(
        key, Utils.mid
    ).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*get Salt*/
    fun getSalt() = mService.getSalt(
        Utils.mid
    ).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())!!

    /*do change password*/
    fun doRsetPassword(userId: Int, otp: String, password: String) =
        mService.doResetPassword(userId, otp, password).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!

    /*cash payment*/
    fun doCashPayment(orderid: String) =
        mService.doCashPaymentStatus(orderid).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!


}


