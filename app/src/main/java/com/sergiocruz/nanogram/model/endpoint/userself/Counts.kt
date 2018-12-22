package com.sergiocruz.nanogram.model.endpoint.userself

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Counts (
    @SerializedName("media")
    @Expose
    var media: Int? = null,
    @SerializedName("follows")
    @Expose
    var follows: Int? = null,
    @SerializedName("followed_by")
    @Expose
    var followedBy: Int? = null
)