package com.sergiocruz.nanogram.model.endpoint.media

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InstagramApiResponseMedia {

    @SerializedName("data")
    @Expose
    var data: List<InstagramMediaData>? = null

}
