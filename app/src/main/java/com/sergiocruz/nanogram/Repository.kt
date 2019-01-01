package com.sergiocruz.nanogram

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class Repository {
    @Throws(InterruptedException::class)
    fun performLongOperation(v: String): Any {
        Observable.just("long item 1", "longer item 2", "longest item 3")
            .doOnNext { item: String? -> println("processing $item on thread ${Thread.currentThread().name}") }
            .subscribeOn(Schedulers.newThread())
            .map { it.length }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { length -> println("item length $length received on thread ${Thread.currentThread().name}") }

        return Unit
    }



}


