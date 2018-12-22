package com.sergiocruz.nanogram.model.endpoint.usermedia

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Images(

    @SerializedName("low_resolution")
    @Expose
    var lowResolution: Resolution? = null,
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: Resolution? = null,
    @SerializedName("standard_resolution")
    @Expose
    var standardResolution: Resolution? = null

)