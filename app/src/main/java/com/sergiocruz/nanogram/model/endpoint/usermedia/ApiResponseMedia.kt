package com.sergiocruz.nanogram.model.endpoint.usermedia

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiResponseMedia {

    @SerializedName("data")
    @Expose
    var data: List<InstagramMediaData>? = null

}
