package com.sergiocruz.nanogram.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.sergiocruz.nanogram.database.AppDatabase
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.model.endpoint.userself.SelfData
import com.sergiocruz.nanogram.viewmodel.ImageVarLiveData
import com.sergiocruz.nanogram.viewmodel.UserInfoLiveData
import timber.log.Timber

object Repository {

    private var userMedia: MutableLiveData<MutableList<ImageVar>> = MutableLiveData()
    private var userInfo: MutableLiveData<SelfData> = MutableLiveData()
    private lateinit var database: AppDatabase
    private lateinit var userToken: String

    operator fun invoke(context: Context, savedToken: String): Repository {
        this.database = AppDatabase.getDatabase(context)
        this.userToken = savedToken
        return this
    }

    fun getUserMedia(): MutableLiveData<MutableList<ImageVar>> {
        if (userMedia.value.isNullOrEmpty()) {
            Timber.w("getting data from source UserMedia")
            userMedia = ImageVarLiveData(userToken, database)
        }
        return userMedia
    }

    fun getUserInfo(): MutableLiveData<SelfData> {
        if (userInfo.value == null) {
            Timber.w("getting data from source UserInfo")
            userInfo = UserInfoLiveData(userToken)
        }
        return userInfo
    }

    //region old code
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
    //endregion

}