package com.sergiocruz.nanogram.model.endpoint.comments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InstagramApiResponseComments(
    @SerializedName("data")
    @Expose
    var data: List<ApiResponseComments>? = null
)