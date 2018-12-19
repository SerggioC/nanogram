package com.sergiocruz.nanogram.model.endpoint.usermedia

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Images {

    @SerializedName("low_resolution")
    @Expose
    var lowResolution: LowResolution? = null
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: Thumbnail? = null
    @SerializedName("standard_resolution")
    @Expose
    var standardResolution: StandardResolution? = null

}