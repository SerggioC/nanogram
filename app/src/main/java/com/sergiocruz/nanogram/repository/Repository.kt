package com.sergiocruz.nanogram.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.viewmodel.ImageVarLiveData

object Repository {

    private var userMedia: MutableLiveData<MutableList<ImageVar>> = MutableLiveData()

//    fun getData(
//        getFavorites: Boolean?,
//        hasInternet: Boolean?,
//        token: String?
//    ): MutableLiveData<MutableList<ImageVar>> {
//        val threadExecutor = ThreadExecutor()
//
//        if (hasInternet!! && (!getFavorites)!!)
//            threadExecutor.networkIO().execute({ getDataFromNetwork(responseData) })
//        else if (!hasInternet || getFavorites!!)
//            threadExecutor.diskIO().execute({ getFavoritesFromDB(responseData) })
//
//        return responseData
//    }

    fun getUserMedia(context: Context): MutableLiveData<MutableList<ImageVar>> {
        if (userMedia.value.isNullOrEmpty()) {
            userMedia = ImageVarLiveData(context)
        }
        return userMedia
    }
}