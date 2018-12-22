package com.sergiocruz.nanogram.model.endpoint.comments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class From(
    @SerializedName("username")
    @Expose
    var username: String? = null,
    @SerializedName("full_name")
    @Expose
    var fullName: String? = null,
    @SerializedName("type")
    @Expose
    var type: String? = null,
    @SerializedName("id")
    @Expose
    var id: String? = null
)