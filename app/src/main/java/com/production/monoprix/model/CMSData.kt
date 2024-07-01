package com.production.monoprix.model

data class CMSData(
    val id: String,
    val cms_title: String,
    val cms_information: String,

    /*For Reorder*/
    val cart_count: Int,

    //My Profile
    var user_id: String,
    var user_name: String,
    var user_email: String,
    var mobile_number: String,
    var country_code: String,
    var gender: String,
    var image: String

)