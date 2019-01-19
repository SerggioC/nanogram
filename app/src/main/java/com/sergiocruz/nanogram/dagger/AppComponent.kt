package com.sergiocruz.nanogram.dagger

import android.content.Context
import com.sergiocruz.nanogram.viewmodel.Car
import dagger.Component

@Component
interface AppComponent {

    fun getMyCarPlease(): Car

    fun inject(context: Context)
}