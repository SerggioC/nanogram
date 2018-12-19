package com.sergiocruz.nanogram.model.endpoint.comments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiResponseComments(

    @SerializedName("created_time")
    @Expose
    var createdTime: String? = null,
    @SerializedName("text")
    @Expose
    var text: String? = null,
    @SerializedName("from")
    @Expose
    var from: From? = null,
    @SerializedName("id")
    @Expose
    var id: String? = null

)