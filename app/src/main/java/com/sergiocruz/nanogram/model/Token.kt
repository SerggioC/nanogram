package com.sergiocruz.nanogram.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sergiocruz.nanogram.model.endpoint.usermedia.User

data class Token(
    @SerializedName("access_token")
    @Expose
    var token: String? = null,
    @SerializedName("user")
    @Expose
    var user: User? = null

)
