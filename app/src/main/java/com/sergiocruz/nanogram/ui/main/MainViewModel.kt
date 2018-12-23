package com.sergiocruz.nanogram.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.model.endpoint.usermedia.ApiResponseMedia
import com.sergiocruz.nanogram.retrofit.InstagramApiControler
import com.sergiocruz.nanogram.util.InfoLevel.ERROR
import com.sergiocruz.nanogram.util.InfoLevel.WARNING
import com.sergiocruz.nanogram.util.getSavedToken
import com.sergiocruz.nanogram.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private var responseData: MutableLiveData<ApiResponseMedia> = MutableLiveData()

    fun getUserMedia(context: Context): MutableLiveData<ApiResponseMedia> {
        val apiController = InstagramApiControler().apiController!!
        val token = getSavedToken(context)
        val userMedia = apiController.getUserMedia(token)
        userMedia.enqueue(object : Callback<ApiResponseMedia> {
            override fun onResponse(
                call: Call<ApiResponseMedia>,
                response: Response<ApiResponseMedia>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    Log.i("Sergio> ", "data: $data")

                    responseData.postValue(response.body())

                } else {
                    showToast(context, context.getString(R.string.error_getting_media), WARNING)
                    Log.w("Sergio> ", "response code: ${response.code()} ${response.errorBody().toString()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponseMedia>, t: Throwable) {
                showToast(context, context.getString(R.string.error_getting_media), ERROR)
                Log.e("Sergio> ", "throwable : ${t.localizedMessage}")
            }

        })

        return responseData
    }

}
