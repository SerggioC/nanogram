package com.sergiocruz.nanogram.viewmodel

import androidx.lifecycle.MutableLiveData
import com.sergiocruz.nanogram.model.endpoint.userself.InstagramApiResponseSelf
import com.sergiocruz.nanogram.model.endpoint.userself.SelfData
import com.sergiocruz.nanogram.retrofit.AppApiController
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class UserInfoLiveData(userToken: String) : MutableLiveData<SelfData>() {

    private var disposable: Disposable? =
        AppApiController.apiController
            ?.getUserInfo(userToken)
            ?.onErrorResumeNext { observer: Observer<in InstagramApiResponseSelf> ->
                Timber.e("gotcha! error getting user Info on thread ${getThread()}, resuming")
                this.postValue(SelfData(fullName = "nanogram User"))
            }
            ?.subscribeOn(Schedulers.io())
            ?.flatMap { result: InstagramApiResponseSelf ->
                Observable.just(result.data)
            }
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { parsedResponse: SelfData? ->
                Timber.w("received parsed response on thread: ${getThread()}")
                this.value = parsedResponse
            }


    private fun getThread() = Thread.currentThread().name

    override fun onInactive() {
        super.onInactive()
        if (disposable?.isDisposed?.not() == true) {
            disposable?.dispose()
        }
    }
}