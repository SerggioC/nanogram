package com.sergiocruz.nanogram.model.endpoint.usermedia

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Videos {

    @SerializedName("low_resolution")
    @Expose
    var lowResolution: LowResolution? = null
    @SerializedName("standard_resolution")
    @Expose
    var standardResolution: StandardResolution? = null
    @SerializedName("comments")
    @Expose
    var comments: Comments? = null
    @SerializedName("caption")
    @Expose
    var caption: Any? = null
    @SerializedName("likes")
    @Expose
    var likes: Likes? = null
    @SerializedName("link")
    @Expose
    var link: String? = null
    @SerializedName("created_time")
    @Expose
    var createdTime: String? = null
    @SerializedName("images")
    @Expose
    private var images: Images? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("users_in_photo")
    @Expose
    var usersInPhoto: Any? = null
    @SerializedName("filter")
    @Expose
    var filter: String? = null
    @SerializedName("tags")
    @Expose
    var tags: List<Any>? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("user")
    @Expose
    var user: User? = null
    @SerializedName("location")
    @Expose
    var location: Any? = null

}
