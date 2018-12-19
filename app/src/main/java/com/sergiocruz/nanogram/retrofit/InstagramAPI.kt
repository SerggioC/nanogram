package com.sergiocruz.nanogram.retrofit

import com.sergiocruz.nanogram.model.Token
import com.sergiocruz.nanogram.model.endpoint.comments.InstagramApiResponseComments
import com.sergiocruz.nanogram.model.endpoint.media.InstagramApiResponseMedia
import com.sergiocruz.nanogram.model.endpoint.media.userself.InstagramApiResponseSelf
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

    /**
     * client_id: your client id
     * client_secret: your client secret
     * grant_type: authorization_code is currently the only supported value
     * redirect_uri: the redirect_uri you used in the authorization request. Note: this has to be the same value as in the authorization request.
     * code: the exact code you received during the authorization step.
     */
    @POST("https://api.instagram.com/oauth/access_token")
    @FormUrlEncoded
    fun getAcessCode(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code") code: String
    ): Call<Token>

    @GET("{root}users/self/?access_token={token}")
    fun getUserInfo(@Path("root") root: String, @Path("token") token: String): Call<InstagramApiResponseSelf>

    @GET("{root}users/self/media/recent/?access_token={token}")
    fun getUserMedia(@Path("root") root: String, @Path("token") token: String): Call<InstagramApiResponseMedia>

    @GET("{root}media/{media-id}/comments?access_token={token}")
    fun getCommentsForMediaId(@Path("root") root: String, @Path("media-id") mediaId: String, @Path("token") token: String): Call<InstagramApiResponseComments>


}