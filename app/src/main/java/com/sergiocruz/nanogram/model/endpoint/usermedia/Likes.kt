package com.sergiocruz.nanogram.model.endpoint.usermedia

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Likes(
    @SerializedName("count")
    @Expose
    var count: Int? = null
)