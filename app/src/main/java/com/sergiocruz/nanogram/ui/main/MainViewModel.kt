package com.sergiocruz.nanogram.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.model.endpoint.userself.SelfData
import com.sergiocruz.nanogram.repository.Repository

class MainViewModel : ViewModel() {

    fun getUserMedia(context: Context): MutableLiveData<MutableList<ImageVar>> {
        return Repository.getUserMedia(context)
    }

    fun getImageVarForIndex(index: Int, context: Context): ImageVar {
        return Repository.getUserMedia(context).value?.get(index)!!
    }

    fun getUserInfo(context: Context) : MutableLiveData<SelfData>{
        return Repository.getUserInfo(context)
    }

}