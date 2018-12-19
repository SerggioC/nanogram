package com.sergiocruz.nanogram.model.endpoint.comments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommnentsFrom {

    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("profile_picture")
    @Expose
    var profilePicture: String? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("full_name")
    @Expose
    var fullName: String? = null

}