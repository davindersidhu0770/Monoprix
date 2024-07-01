package com.production.monoprix.model


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class StatusResponseModel(
    /*login*/
    @Json(name = "code")
    val countryCode: String,

    @Json(name = "countryId")
    val countryId: String,

    @Json(name = "title")
    val gender: String,

    @Json(name = "login_token")
    val loginToken: String,

    @Json(name = "mobileNumber")
    val mobileNumber: String,

    @Json(name = "email")
    val userEmail: String,

    @Json(name = "userId")
    val userId: String,

    @Json(name = "name")
    val userName: String,

    @Json(name = "country")
    val country: String,

    @Json(name = "familyName")
    val familyName: String,
    val password: String,
    val countryname: String,
    val deviceId: String,

    /*banner*/
    val banner: List<BannerList>,
    /*store list*/
    val shop_details: List<ShopDetails>,
    /*store details*/
    val clientStoreId: String,
    val location: String,
    val address: String,
    @Json(name = "imagepath")
    val storeImg: String,
    val name_Arabic: String,
    val address_Arabic: String,
    /*cart*/
    val cartitems: List<CartList>,
    @Json(name = "delivery_charge")
    val deliveryCharge: Int,
    @Json(name = "subtotalamount")
    val subTotalAmount: String,
    @Json(name = "payableamount")
    val total: String,
    val is_homedelivery_available: Boolean,
    val delivery_message: String,
    val delivery_message_arabic: String,
    val loyalty_message_title: String,
    val loyalty_message_description: String,
    val loyalty_message_title_ar: String,
    val loyalty_message_description_ar: String,
    val loyaltyBalance: Double,

    val currentDate: String,

    /*cms*/
    val id: String,
    val cms_title: String,
    val termOfService: String,

    /*time slot*/
    @Json(name = "allowed_days")
    val allowedDays: String,
    @Json(name = "deliver_now_time")
    val deliverNowTime: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "time_slots")
    val timeSlots: List<TimeSlot>,

    /*payment*/
    @Json(name = "orderDate")
    val orderDate: String,
    @Json(name = "deliveryDate")
    val deliveryDate: String,
    @Json(name = "orderId")
    val orderID: String,
    @Json(name = "trackingID")
    val trackingID: String,
    @Json(name = "paymenturl")
    val payment_url: String,
    @Json(name = "invoiceAmount")
    val invoiceAmount: String,
    @Json(name = "pointsRedeemed")
    val pointsRedeemed: String,

    val qrCodeImageUrl: String,
    val loyaltyPoints: Double,
    val pointsEarned: Double,

    /*contact us*/
    val contactUsNo: String,
    val contactUsFb: String,
    val socialmedia:List<SocialModel>,

    /*loyalty*/
    val loyaltyId: String?,
    val cardno: String?,
    val amount: String?,
    val csmroleID:String,
    val isActive:Boolean,
    val consumerid:Long,
    val barcode:String,
    var points:Double,
    val barcodePath:String,
    val dob:String,
    val area:String,

    /*resetpassword*/
    val mobile: String,

    /*salt*/
    val maxOTPTry: String = "3",
    val otpMaxMsg: String = "You have reached maximum allowed tries. Please try again later.",
    val key: String,
    val userradious: String



)