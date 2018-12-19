package com.sergiocruz.nanogram.model.endpoint.media.userself

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InstagramApiResponseSelf {

    @SerializedName("data")
    @Expose
    var data: SelfData? = null

}