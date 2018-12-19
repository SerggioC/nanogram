package com.sergiocruz.nanogram.retrofit

import com.sergiocruz.nanogram.model.endpoint.comments.InstagramApiResponseComments
import com.sergiocruz.nanogram.model.endpoint.media.InstagramApiResponseMedia
import com.sergiocruz.nanogram.model.endpoint.media.userself.InstagramApiResponseSelf
import com.sergiocruz.nanogram.model.endpoint.media.InstagramMediaData
import retrofit2.Call
import retrofit2.http.*

/**
 * Instagram API root URL*/
val ROOT_URL = "https://api.instagram.com/v1/"

/**
 * GET /users/selfGet information about the owner of the access token.
 * User info endpoint
 * */
const val userInfoUrl = "https://api.instagram.com/v1/users/self/?access_token=ACCESS-TOKEN"

/**
 * GET /users/self/media/recentGet the most recent media of the user.
 * user Media Endpoint
 * */
const val userMediaUrl =
    "https://api.instagram.com/v1/users/self/media/recent/?access_token=ACCESS-TOKEN"

/**
 * https://api.instagram.com/v1/media/{media-id}/comments?access_token=ACCESS-TOKEN
 * Comments Endpoint for media-id
 * */
const val commentsUrl =
    "https://api.instagram.com/v1/media/{media-id}/comments?access_token=ACCESS-TOKEN"


interface InstagramAPI {

    @POST("/posts")
    @FormUrlEncoded
    fun savePost(
        @Field("title") title: String,
        @Field("body") body: String,
        @Field("userId") userId: Long
    ): Call<InstagramMediaData>


    @GET("{root}users/self/?access_token={token}")
    fun getUserInfo(@Path("root") root: String, @Path("token") token: String): Call<InstagramApiResponseSelf>

    @GET("{root}users/self/media/recent/?access_token={token}")
    fun getUserMedia(@Path("root") root: String, @Path("token") token: String): Call<InstagramApiResponseMedia>

    @GET("{root}media/{media-id}/comments?access_token={token}")
    fun getCommentsForMediaId(@Path("root") root: String, @Path("media-id") mediaId: String, @Path("token") token: String): Call<InstagramApiResponseComments>


}