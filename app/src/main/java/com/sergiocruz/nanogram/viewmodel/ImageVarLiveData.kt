package com.sergiocruz.nanogram.viewmodel

import androidx.lifecycle.MutableLiveData
import com.sergiocruz.nanogram.database.AppDatabase
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.model.endpoint.usermedia.ApiResponseMedia
import com.sergiocruz.nanogram.model.endpoint.usermedia.InstagramMediaData
import com.sergiocruz.nanogram.retrofit.AppApiController
import io.reactivex.Observable
import kotlinx.coroutines.*
import timber.log.Timber

class ImageVarLiveData(private val userToken: String, var database: AppDatabase) :
    MutableLiveData<MutableList<ImageVar>>() {

    private val coroutineJob: Job by lazy { SupervisorJob() }
    private val coroutine by lazy { CoroutineScope(Dispatchers.IO + coroutineJob) }

    init {


        coroutine.launch {

            val result = withTimeoutOrNull(2900L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
                "Done" // will get cancelled before it produces this result
            }

            println("Result is $result")

            try {
                val asyncResponse = AppApiController
                    .instagramLazyAPI
                    .getUserMediaAsync(userToken)
                    .await()

                if (asyncResponse.isSuccessful) {
                    val body: ApiResponseMedia? = asyncResponse.body()
                    val parsedResponse = parseResponseCoroutine(body)
                    this@ImageVarLiveData.postValue(parsedResponse)
                    saveToLocalDatabase(parsedResponse)
                } else {
                    this@ImageVarLiveData.postValue(getDataFromLocalDatabase())
                }
            } catch (e: Exception) {
                Timber.e("Some unexpected error happened getting ImageVarLiveData: $e")
                this@ImageVarLiveData.postValue(getDataFromLocalDatabase())
            }
        }
    }

    private fun getThread() = Thread.currentThread().name

    private fun getDataFromLocalDatabase(): MutableList<ImageVar>? {
        Timber.d("getting data from local db on thread ${Thread.currentThread().name}")
        return database.databaseDao().getAllPosts
    }

    private fun saveToLocalDatabase(data: MutableList<ImageVar>) {
        Timber.d("Clearing DB and saving data on thread ${Thread.currentThread().name}")
        database.databaseDao().clearAndInsert(data)
    }

    /** Extract the ImageVar list from InstagramMediaData response */
    private fun parseResponseCoroutine(response: ApiResponseMedia?): MutableList<ImageVar> {
        Timber.w("parsing list on thread: ${Thread.currentThread().name}")
        val time = System.currentTimeMillis()

        val data: List<InstagramMediaData>? = response?.data
        val imageList =
            (0 until data!!.size)
                .filter { data[it].type == "image" }
                .map { index ->
                    ImageVar(
                        null,
                        data[index].images,
                        data[index].likes,
                        data[index].comments,
                        data[index].caption
                    )
                } as MutableList

        data.forEach {
            if (it.type == "carousel") {
                val images =
                    List(it.carouselMedia!!.size) { index ->
                        ImageVar(
                            null,
                            it.carouselMedia!![index].images,
                            it.likes,
                            it.comments,
                            it.caption
                        )
                    }
                imageList.addAll(images)
            }
        }

        Timber.w("list parsing done in ${System.currentTimeMillis().minus(time)} milliseconds")
        return imageList
    }

    /** Extract the ImageVar list from InstagramMediaData response RX mode*/
    private fun parseResponseRxMode(response: ApiResponseMedia?): Observable<MutableList<ImageVar>> {
        Timber.w("parsing list on thread: ${Thread.currentThread().name}")
        val time = System.currentTimeMillis()

        val data: List<InstagramMediaData>? = response?.data
        val imageList =
            (0 until data!!.size)
                .filter { data[it].type == "image" }
                .map { index ->
                    ImageVar(
                        null,
                        data[index].images,
                        data[index].likes,
                        data[index].comments,
                        data[index].caption
                    )
                } as MutableList

        data.forEach {
            if (it.type == "carousel") {
                val images =
                    List(it.carouselMedia!!.size) { index ->
                        ImageVar(
                            null,
                            it.carouselMedia!![index].images,
                            it.likes,
                            it.comments,
                            it.caption
                        )
                    }
                imageList.addAll(images)
            }
        }

        Timber.w("list parsing done in ${System.currentTimeMillis().minus(time)} milliseconds")
        return Observable.just(imageList)
    }

    override fun onInactive() {
        super.onInactive()
        coroutineJob.cancel()
    }

}
