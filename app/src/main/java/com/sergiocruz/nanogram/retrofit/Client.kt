package com.sergiocruz.nanogram.retrofit

import android.net.Proxy
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class Client {

    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {
        if (retrofit == null) {
            val okHttpClient = OkHttpClient().newBuilder().addInterceptor(LoggingInterceptor()).build()
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }



    @Throws(IOException::class)
    fun authenticate(proxy: Proxy, response: Response): Request {
        // Refresh your access_token using a synchronous api request
        newAccessToken = service.refreshToken()

        // Add new header to rejected request and retry it
        return response.request().newBuilder()
            .addHeader(AUTHORIZATION, newAccessToken)
            .build()
    }
}

internal class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        logger.info(
            String.format(
                "Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()
            )
        )

        val response = chain.proceed(request)

        val t2 = System.nanoTime()
        logger.info(
            String.format(
                "Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6, response.headers()
            )
        )

        return response
    }
}