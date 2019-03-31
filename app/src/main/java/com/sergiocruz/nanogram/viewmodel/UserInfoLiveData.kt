package com.sergiocruz.nanogram.viewmodel

import androidx.lifecycle.MutableLiveData
import com.sergiocruz.nanogram.model.endpoint.userself.InstagramApiResponseSelf
import com.sergiocruz.nanogram.model.endpoint.userself.SelfData
import com.sergiocruz.nanogram.retrofit.AppApiController
import kotlinx.coroutines.*
import retrofit2.Response
import timber.log.Timber

class UserInfoLiveData(private val userToken: String) : MutableLiveData<SelfData>() {

    private val coroutineJob: Job by lazy { SupervisorJob() }
    private val coroutine by lazy { CoroutineScope(Dispatchers.IO + coroutineJob) }

    init {
        coroutine.launch {
            try {
                val result: Response<InstagramApiResponseSelf> = AppApiController
                    .instagramLazyAPI
                    .getUserInfoAsync(userToken)
                    .await()

                if (result.isSuccessful) {
                    val data: SelfData? = result.body()?.data
                    this@UserInfoLiveData.postValue(data)
                } else {
                    this@UserInfoLiveData.postValue(SelfData(fullName = "Nanogram User"))
                }
            } catch (e: Exception) {
                Timber.e("Some unexpected error happened getting UserInfoLiveData: $e")
            }

        }
    }

    private fun getThread() = Thread.currentThread().name

    override fun onInactive() {
        super.onInactive()
        coroutineJob.cancel()
    }
}