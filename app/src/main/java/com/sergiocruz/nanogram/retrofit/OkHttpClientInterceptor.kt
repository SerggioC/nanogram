package com.sergiocruz.nanogram.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException


class OkHttpClientInterceptor : OkHttpClient() {
    override fun networkInterceptors(): MutableList<Interceptor> {

        return super.networkInterceptors()
    }
}

@Throws(IOException::class)
fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
// Nothing to add to intercepted request if:
    // a) Authorization value is empty because user is not logged in yet
    // b) There is already a header with updated Authorization value
    if (authorizationTokenIsEmpty() || alreadyHasAuthorizationHeader(originalRequest)) {
        return chain.proceed(originalRequest);
    }
    // Add authorization header with updated authorization value to intercepted request
    val authorisedRequest = originalRequest.newBuilder()
        .header(AUTHORIZATION, accessToken)
        .build()
    return chain.proceed(authorisedRequest)
}
