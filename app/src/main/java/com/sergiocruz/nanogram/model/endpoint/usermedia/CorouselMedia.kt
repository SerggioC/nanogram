package com.sergiocruz.nanogram.model.endpoint.usermedia

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class CarouselMedia(

    @SerializedName("images")
    @Expose
    var images: Images? = null,
    @SerializedName("users_in_photo")
    @Expose
    var usersInPhoto: List<Any>? = null,
    @SerializedName("type")
    @Expose
    var type: String? = null

)