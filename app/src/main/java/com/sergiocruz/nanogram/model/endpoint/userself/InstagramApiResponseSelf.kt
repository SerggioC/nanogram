package com.sergiocruz.nanogram.model.endpoint.userself

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InstagramApiResponseSelf(
    @SerializedName("data")
    @Expose
    var data: SelfData? = null
)