package com.sergiocruz.nanogram.retrofit

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Get data using retrofit and serialize automatically with GSON
 */
class InstagramApiControler {
    private var instagramAPI: InstagramAPI? = null

    val apiController: InstagramAPI?
        get() {
            if (instagramAPI != null) return instagramAPI

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            instagramAPI = retrofit.create(InstagramAPI::class.java!!)

            return instagramAPI
        }
}