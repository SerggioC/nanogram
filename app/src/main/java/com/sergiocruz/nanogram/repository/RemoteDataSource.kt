package com.sergiocruz.nanogram.repository

import com.sergiocruz.nanogram.model.ImageVar
import io.reactivex.Observable


interface RemoteDataSource {

    fun getRemoteData() : Observable<MutableList<ImageVar>>

}