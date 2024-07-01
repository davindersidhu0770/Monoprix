package com.production.monoprix.api


import com.production.monoprix.model.*
import com.production.monoprix.model.StatusProductByCodeModel
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.http.*
import rx.Observable

/**
 * Created by chaithra on 10/6/16.
 */

interface ApiService {

    @FormUrlEncoded
    @POST("IsLoginValidV2")
    fun getLogin(
        @Field("email") user_email: String,
        @Field("password") password: String,
        @Field("device_id") device_id: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
        @Field("FMCToken") fmctoken: String,
        @Field("isFingerLogin") isFingerLogin: Boolean,
        @Field("platform") platform: String,
        @Field("deviceId") deviceId: String
    ): Observable<StatusModel>

    @FormUrlEncoded
    @POST("AddAccount")
    fun doSignUP(
        @Field("Title") title: String,
        @Field("Name") name: String,
        @Field("FamilyName") familyName: String,
        @Field("Email") email: String,
        @Field("CountryId") countryId: Int,
        @Field("MobileNumber") mobile: String,
        @Field("Password") password: String,
        @Field("Flag18years") flagyear: Boolean,
        @Field("flgTermsOfService") flagterms: Boolean,
        @Field("platform") platform: String,
        @Field("deviceId") deviceId: String,
        @Field("dob") dob: String,
        @Field("country") country: String,
        @Field("area") area: String

    ): Observable<StatusModel>

    /*verify otp*/
    @FormUrlEncoded
    @POST("IsOTPValid")
    fun getOtp(
        @Field("UserId") userId: Int,
        @Field("otp") otp: String,
        @Field("email") email: String,
        @Field("mobile") mobile: String,
        @Field("IsAccUpdate") IsAccUpdate: Boolean,
        @Field("platform") platform: String
    ): Observable<StatusRegisterModel>

    /*resend otp*/
    @FormUrlEncoded
    @POST("ResendOTP")
    fun doResendOtp(
        @Field("userid") mobile_number: Int,
        @Field("platform") platform: String
    ): Observable<StatusRegisterModel>

    /*forgot password*/
    @FormUrlEncoded
    @POST("PasswordReset")
    fun doForgotPassword(
        @Field("parameter") parameter: String,
        @Field("type") type: String,
        @Field("platform") platform: String
    ): Observable<StatusModel>

    /*google and fb sign in*/
    @FormUrlEncoded
    @POST("SocialSigninV2")
    fun doSocialLogin(
        @Field("Title") Title: String,
        @Field("Name") Name: String,
        @Field("FamilyName") FamilyName: String,
        @Field("Email") Email: String,
        @Field("CountryId") CountryId: Int,
        @Field("MobileNumber") MobileNumber: String,
        @Field("Flag18years") Flag18years: Boolean,
        @Field("flgTermsOfService") flgTermsOfService: Boolean,
        @Field("socialmediaId") socialmediaId: String,
        @Field("socialmediatoken") socialmediatoken: String,
        @Field("Islogin") Islogin: Boolean,
        @Field("socialtype") social_type: String,
        @Field("platform") platform: String,
        @Field("FMCToken") fmctoken: String,
        @Field("deviceId") deviceId: String
    ): Observable<StatusModel>

    /*stores*/
    @GET("GetStores")
    fun getStores(): Observable<CountryModel>

    @GET("getZones")
    fun getZones(): Observable<ZoneModel>

    //Store List
    @FormUrlEncoded
    @POST("getStoreByLocation")
    fun getStoreLocation(
        @Field("location") location: String
    ): Observable<StatusModel>

    //AddressList
    @GET("getAddressByUserId/{id}")
    fun getShippingAddressList(
        @Path("id") user_id: String
    ): Observable<AddressStatus>

    /*update address*/
    @FormUrlEncoded
    @POST("updateAddressById")
    fun getEditShippingAddress(
        @Field("AddressId") shipping_address_id: Int,
        @Field("Title") title: String,
        @Field("UserId") user_id: Int,
        @Field("AreaId") area_id: Int,
        @Field("AddressDesp") address: String,
        @Field("Location") location: String,
        @Field("Postcode") postcode: String,
        @Field("Mobile") mobile_number: String,
        @Field("LandMark") landmark: String,
        @Field("Building") building: String,
        @Field("HouseNo") houseNo: String,
        @Field("Street") Street: String,
        @Field("CityId") city_id: Int,
        @Field("CountryId") country_code: Int,
        @Field("Isdefault") is_defalut_shipping_address: Boolean,
        @Field("ZoneId") zoneId: String,
        @Field("Muncipality") Muncipality: String,
        @Field("Place") Place: String,
        @Field("Area") area: String
    ): Observable<StatusModel>

    //Add Address
    @FormUrlEncoded
    @POST("addAddress")
    fun addNewAddressDetails(
        @Field("Title") title: String,
        @Field("UserId") user_id: Int,
        @Field("AreaId") area_id: Int,
        @Field("AddressDesp") address_desc: String,
        @Field("Location") location: String,
        @Field("Postcode") postcode: String,
        @Field("Mobile") mobile_num: String,
        @Field("LandMark") landmark: String,
        @Field("Building") building: String,
        @Field("HouseNo") houseNo: String,
        @Field("Street") Street: String,
        @Field("CityId") country_code: Int,
        @Field("CountryId") country_id: Int,
        @Field("Isdefault") is_defalut_address: Boolean,
        @Field("ZoneId") zoneId: String,
        @Field("Muncipality") Muncipality: String,
        @Field("Place") Place: String,
        @Field("Area") area: String

    ): Observable<StatusModel>

    //Get City List
    @GET("getCityByCountryId/{id}")
    fun getCityList(
        @Path("id") countryId: Int
    ): Observable<CountryModel>


    //GetArea List
    @GET("getAreaByCityId/{id}")
    fun getAreaList(
        @Path("id") city_id: Int
    ): Observable<CountryModel>

    /*  //CMS List
      @GET("GetTerms")
      fun getCMS(): Observable<StatusModel>*/

    //CMS List
    @GET("GetLoyaltyProgram_Terms_Conditions")
    fun getCMS(): Observable<TermsAndConditionsModel>

    //CMS List
    /*@GET("GetTerms_Conditions")
    fun getCMSTermsAndConditions(): Observable<TermsModel>*/

    @GET("GetLoyaltyProgram_Terms_Conditions")
    fun getCMSTermsAndConditions(): Observable<TermsAndConditionsModel>

    @GET("GetPrivacy_Policy")
    fun getPrivacyPolicy(): Observable<PrivacyPolicyModel>

    @GET("GetLoyaltyFAQ")
    fun getFaq(): Observable<FaqModel>

    @GET("GetHelp")
    fun getHelp(): Observable<HelpModel>

    @GET("GetBenefit")
    fun getBenefit(): Observable<BenefitModel>

    /*product list*/
    /*checkout or cart summary*/
    @FormUrlEncoded
    @POST("getCartProduct")
    fun getProductList(
        @Field("userId") userId: String,
        @Field("cartId") cartId: Int
    ): Observable<StatusModel>

    /*add to cart*/
    @FormUrlEncoded
    @POST("addProductToCart")
    fun doAddProduct(
        @Field("cartvalue") strArray: JSONArray
    ): Observable<StatusModel>

    /*add to cart*/
    @FormUrlEncoded
    @POST("RedeemLoyalty")
    fun RedeemLoyalty(
        @Field("userId") userId: String,
        @Field("total") total: String,
        @Field("loyaltybalance") loyaltybalance: String,
        @Field("amountToRedeem") points: String
    ): Observable<RedeemPointModel>

    /*cart update*/
    @FormUrlEncoded
    @POST("updateProductInCart")
    fun getCartUpdate(
        @Field("carddetailId") carddetailId: String,
        @Field("quantity") quantity: String
    ): Observable<StatusModel>

    /*cart delete*/
    @FormUrlEncoded
    @POST("deleteCartById")
    fun doCartDelete(
        @Field("cartdetailId") cart_item_id: String
    ): Observable<StatusModel>

    /*clear cart*/
    @FormUrlEncoded
    @POST("clearCart")
    fun doCartClear(
        @Field("cartid") cartid: String
    ): Observable<StatusModel>

    /*getProductByBarcode*/
    @FormUrlEncoded
    @POST("getProductByBarcode")
    fun getProductDetails(
        @Field("storeId") storeId: String,
        @Field("barcode") code: String,
        @Field("location") location: String,
        @Field("userId") userId: String
    ): Observable<StatusProductByCodeModel>

    /*token*/
    @FormUrlEncoded
    @POST("getTokennew")
    fun getToken(
        @Field("key", encoded = true) key: String,
        @Field("mid") mid: String
    ): Observable<StatusTokenModel>

    /*get time slot*/
    @GET("getDeliveryTime")
    fun getTimeSlot(): Observable<StatusTimeSlotModel>


    /*country list*/
    @GET("getCountries")
    fun getCountries(): Observable<CountryModel>

    /*payment*/
    /*@FormUrlEncoded
    @POST("createOrder")
    fun doPayment(
        @Field("cartId") cartId: String,
        @Field("userId") userId: String,
        @Field("paymentmode") paymentmode: String,
        @Field("Isdeliveryopted") Isdeliveryopted: Boolean,
        @Field("addressId") addressId: Int,
        @Field("deliveryslotId") deliveryslotId: Int,
        @Field("language") language: String
    ): Observable<StatusModel>*/
    @FormUrlEncoded
    @POST("createOrder_Staging")
    fun doPayment(
        @Field("cartId") cartId: String,
        @Field("userId") userId: String,
        @Field("paymentmode") paymentmode: String,
        @Field("Isdeliveryopted") Isdeliveryopted: Boolean,
        @Field("addressId") addressId: Int,
        @Field("deliveryslotId") deliveryslotId: Int,
        @Field("language") language: String,
        @Field("loyaltyRedeemed") loyaltyRedeemed: String
    ): Observable<StatusModel>

    /*update account*/
    @FormUrlEncoded
    @POST("UpdateAccount")
    fun doUpdateAccount(
        @Field("UserId") UserId: Int,
        @Field("Title") Title: String,
        @Field("Name") Name: String,
        @Field("FamilyName") FamilyName: String,
        @Field("Email") Email: String,
        @Field("CountryId") CountryId: Int,
        @Field("country") country:String,
        @Field("MobileNumber") MobileNumber: String,
        @Field("Password") Password: String,
        @Field("platform") platform: String,
        @Field("Loyaltyno") loyaltynumber: String,
        @Field("area") area: String,
        @Field("dob") dob: String
    ): Observable<MyAccountModel>

    /*change password*/
    @FormUrlEncoded
    @POST("ChangePassword")
    fun doChangePassword(
        @Field("userId") userId: Int,
        @Field("password") confirmpassword: String,
        @Field("confirmpassword") confirmpassw: String,
        @Field("platform") platform: String,
        @Field("email") email: String,

    ): Observable<ChangePasswordModel>

    /*order history list*/
    /*@GET("getOrderList/{id}")
    fun getOrderHistoryList(
        @Path("id") id : Int
    ): Observable<StatusOrderHistoryModel>*/
    @GET("getOrderList_Staging/{id}")
    fun getOrderHistoryList(
        @Path("id") id: Int
    ): Observable<StatusOrderHistoryModel>

    /*order details*/
    @GET("getOrderDetailByOrderId/{id}")
    fun getOrderDetails(
        @Path("id") userId: Int
    ): Observable<StatusOrderDetaileModel>

    /*DeviceIDChangeRequestEmail*/
    @GET("DeviceIDChangeRequestEmail")
    fun DeviceIDChangeRequestEmail(
        @Query("userId") userId: String,
        @Query("deviceId") deviceId: String
    ): Observable<DeviceUpdateStatusModel>

    /*DeviceIDUpdated*/
    @GET("DeviceIDUpdated")
    fun DeviceIDUpdated(
        @Query("userId") userId: String,
        @Query("deviceId") deviceId: String
    ): Observable<StatusDiviceIDUpdateModel>

    /*get contact us*/
    @GET("GetContactUs")
    fun getContactUs(): Observable<StatusModel>

    /*submit contact us*/
    @FormUrlEncoded
    @POST("ContactUs")
    fun doContactUs(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("mobile") mobile: String,
        @Field("message") message: String
    ): Observable<StatusTokenModel>

    /*banners*/
    @GET("PromotionImages")
    fun getBanner(): Observable<StatusBannerModel>/*

    *//*banners*//*
    @GET("GetBanner")
    fun getBanner() : Observable<StatusBannerModel>*/

    /*PromotionOnline*/  //Added by Jaideep on 12-jan-2022.
    @GET("PromotionOnline")
    fun getPromotionOnline(): Observable<PromotionOnlineModel>

    /*banners*/
    @GET("GetPromotionImages")
    fun getPromotionBanner(): Observable<StatusBannerModel>

    /*loylity*/
    @FormUrlEncoded
    @POST("GetLoyalty")
    fun getLoylity(
        @Field("UserId") UserId: Int
    ): Observable<LoylityModel>

    /*@GET("getAccountbyId/{id}")
    fun getAccountDetails(
        @Path("id") id: Int
    ): Observable<StatusModel>*/

    @GET("getAccountbyId_new/{id}")
    fun getAccountDetails(
        @Path("id") id: Int
    ): Observable<StatusModel>

    @FormUrlEncoded
    @POST("deleteAddressById")
    fun delAddressById(
        @Field("id") id: String,
        @Field("userid") userid: String
    ): Observable<AddressStatus>

    @GET("deleteAccountbyId/{id}")
    fun delAccount(
        @Path("id") id: String
    ): Observable<DelUserModel>

    /*promotions*/
    @GET("GetPromotions")
    fun getPromotion(): Observable<CountryModel>

    /*get salt*/
    @FormUrlEncoded
    @POST("GetSalt")
    fun getSalt(
        @Field("mid") mid: String
    ): Observable<StatusModel>

    /*change password*/
    @FormUrlEncoded
    @POST("ChangePassByMobile")
    fun doResetPassword(
        @Field("UserId") UserId: Int,
        @Field("OTP") otp: String,
        @Field("password") password: String
    ): Observable<ChangePasswordModel>

    /*cash paymnet*/
    @FormUrlEncoded
    @POST("CashPaymentStatus")
    fun doCashPaymentStatus(
        @Field("orderid") orderid: String
    ): Observable<StatusCashPaymentModel>


}