package com.sergiocruz.nanogram.model.endpoint.usermedia

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Location {

    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("street_address")
    @Expose
    var streetAddress: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

}