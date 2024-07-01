package com.production.monoprix.model




data class OrderHistoryList(
    val addressId: Int,
    val creationDatetime: String,
    val deliveryStatus: String,
    val deliveryStatusArabic: String,
    val deliveryTimeId: Int,
    val goldInvoiceId: String,
    val invoiceAmount: Double,
    val isDeliveryOpted: Boolean,
    val isPaid: Boolean,
    val orderId: Int,
    val orderNo: String,
    val paymentMode: String,
    val qrCodeImageUrl: String,
    val storeId: Int,
    val userId: Int,
    val xmlFileFlag: Boolean,
    val productName: String,
    val noofitems: String,
    val loyaltyPoints: Double

)