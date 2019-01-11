package com.sergiocruz.nanogram.repository

import com.sergiocruz.nanogram.model.ImageVar
import io.reactivex.Observable


interface LocalDataSource {

    fun saveLocalData(data: MutableList<ImageVar>)

    fun getLocalData() : Observable<MutableList<ImageVar>>

}