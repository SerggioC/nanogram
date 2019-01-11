package com.sergiocruz.nanogram.retrofit

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Get data using retrofit and serialize automatically with GSON
 */
class AppApiController {
    private var instagramAPI: InstagramAPI? = null

    val apiController: InstagramAPI?
        get() {
            if (instagramAPI != null) return instagramAPI

            val gson = GsonBuilder()
                .setLenient()
                .create()

//            val okHttpClient = OkHttpClient()
//            okHttpClient.interceptors().add(CustomResponseInterceptor())

            val retrofit = Retrofit.Builder()
                .baseUrl(API_ROOT_URL)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            instagramAPI = retrofit.create(InstagramAPI::class.java)

            return instagramAPI
        }



}
