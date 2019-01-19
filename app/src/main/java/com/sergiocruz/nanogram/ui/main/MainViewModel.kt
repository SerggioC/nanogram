package com.sergiocruz.nanogram.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.model.endpoint.userself.SelfData
import com.sergiocruz.nanogram.repository.Repository
import com.sergiocruz.nanogram.util.getSavedToken

class MainViewModel constructor(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.invoke(application, getSavedToken(application))

    fun getUserMedia(): MutableLiveData<MutableList<ImageVar>> {
        return repository.getUserMedia()
    }

    fun getImageVarForIndex(index: Int): ImageVar {
        return repository.getUserMedia().value?.get(index)!!
    }

    fun getUserInfo(): MutableLiveData<SelfData> {
        return repository.getUserInfo()
    }
}