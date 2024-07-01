package com.production.monoprix.model




data class OrderDetailModel(
    val address: String,
    val deliveryStatus: String,
    val deliveryStatusArabic: String,
    val invoiceAmount: Double,
    val isPaid: Boolean,
    val orderId: Int,
    val orderNo: String,
    val paymentMode: String,
    val products: List<Product>,
    val timeSlot: Any,
    val subTotal: Double,
    val loyaltyEarned: Double,
    val loyaltyRedeemed: Double,
    val cartId: Int

)