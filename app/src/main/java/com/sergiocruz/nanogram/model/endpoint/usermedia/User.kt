package com.sergiocruz.nanogram.model.endpoint.usermedia

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("full_name")
    @Expose
    var fullName: String? = null
    @SerializedName("profile_picture")
    @Expose
    var profilePicture: String? = null

}