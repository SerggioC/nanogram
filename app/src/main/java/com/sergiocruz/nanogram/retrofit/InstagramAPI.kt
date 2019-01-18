package com.sergiocruz.nanogram.retrofit

import com.sergiocruz.nanogram.model.Token
import com.sergiocruz.nanogram.model.endpoint.comments.InstagramApiResponseComments
import com.sergiocruz.nanogram.model.endpoint.usermedia.ApiResponseMedia
import com.sergiocruz.nanogram.model.endpoint.userself.InstagramApiResponseSelf
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

/**
 * Instagram API root URL*/
const val API_ROOT_URL = "https://api.instagram.com/v1/"

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
    fun getAccessCode(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code") code: String
    ): Call<Token>

    /** query appended to url with ?access_token=token */
    @GET("https://api.instagram.com/v1/users/self/")
    fun getUserInfo(@Query("access_token") token: String): Observable<InstagramApiResponseSelf>

    @GET("https://api.instagram.com/v1/users/self/media/recent/")
    fun getUserMedia(@Query("access_token") token: String): Observable<ApiResponseMedia>

    @GET("{api_root_url}users/self/media/recent/")
    fun getUserMedia1(
        @Path("api_root_url") root: String,
        @Query("access_token") token: String
    ): Call<ApiResponseMedia>

    @GET("{api_root_url}media/{media-id}/comments")
    fun getCommentsForMediaId(
        @Path("api_root_url") root: String,
        @Path("media-id") mediaId: String,
        @Query("access_token") token: String
    ): Observable<InstagramApiResponseComments>


}