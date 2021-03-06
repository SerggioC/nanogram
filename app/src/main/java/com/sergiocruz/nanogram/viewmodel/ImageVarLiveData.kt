package com.sergiocruz.nanogram.viewmodel

import androidx.lifecycle.MutableLiveData
import com.sergiocruz.nanogram.database.AppDatabase
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.model.endpoint.usermedia.ApiResponseMedia
import com.sergiocruz.nanogram.model.endpoint.usermedia.InstagramMediaData
import com.sergiocruz.nanogram.retrofit.AppApiController
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ImageVarLiveData(userToken: String, var database: AppDatabase) : MutableLiveData<MutableList<ImageVar>>() {

    private var disposable: Disposable? =
        AppApiController.apiController
            ?.getUserMedia(userToken)
            ?.onErrorResumeNext { observer: Observer<in ApiResponseMedia> ->
                Timber.e("gotcha! error on thread ${getThread()}, resuming")
                this.postValue(getDataFromLocalDatabase())
            }
            ?.subscribeOn(Schedulers.io())
            ?.flatMap { result: ApiResponseMedia ->
                parseResponse(result)
                    /** Can use a new thread to save to database
                     * Using previous Schedulers.io */
                    //.subscribeOn(Schedulers.computation())
                    .doOnNext { data: MutableList<ImageVar> ->
                        Timber.w("saving to database on thread ${getThread()} data size: ${data.size}")
                        saveToLocalDatabase(data)
                    }
            }
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { parsedResponse: MutableList<ImageVar>? ->
                Timber.w("received parsed response on thread: ${getThread()}")
                this.value = parsedResponse
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
    private fun parseResponse(response: ApiResponseMedia?): Observable<MutableList<ImageVar>> {
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
        if (disposable?.isDisposed?.not() == true) {
            disposable?.dispose()
        }
    }

}
