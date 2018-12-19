package com.sergiocruz.nanogram.model.endpoint.media

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sergiocruz.nanogram.model.endpoint.usermedia.*

class InstagramMediaData {

    @SerializedName("comments")
    @Expose
    var comments: Comments? = null
    @SerializedName("caption")
    @Expose
    var caption: Caption? = null
    @SerializedName("likes")
    @Expose
    var likes: Likes? = null
    @SerializedName("link")
    @Expose
    var link: String? = null
    @SerializedName("user")
    @Expose
    var user: User? = null
    @SerializedName("created_time")
    @Expose
    var createdTime: String? = null
    @SerializedName("images")
    @Expose
    var images: Images? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("users_in_photo")
    @Expose
    var usersInPhoto: List<Any>? = null
    @SerializedName("filter")
    @Expose
    var filter: String? = null
    @SerializedName("tags")
    @Expose
    var tags: List<String>? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("location")
    @Expose
    var location: Location? = null
    @SerializedName("videos")
    @Expose
    var videos: Videos? = null

}