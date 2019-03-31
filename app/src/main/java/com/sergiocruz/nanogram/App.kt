package com.sergiocruz.nanogram

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.sergiocruz.nanogram.di.AppComponent
import com.sergiocruz.nanogram.di.AppModule
import com.sergiocruz.nanogram.di.DaggerAppComponent
import com.sergiocruz.nanogram.util.TimberImplementation
import com.squareup.leakcanary.LeakCanary

class App : Application() {

    private val component: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        installLeakCanary()
        installTimber()
        component.inject(this)
    }

    private fun installTimber() {
        TimberImplementation.init()
    }

    private fun installLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }

}