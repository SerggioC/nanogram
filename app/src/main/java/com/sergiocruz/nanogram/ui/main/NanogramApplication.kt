package com.sergiocruz.nanogram.ui.main

import android.app.Application
import com.sergiocruz.nanogram.util.TimberImplementation
import com.squareup.leakcanary.LeakCanary

class NanogramApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        installLeakCanary()
    }

    private fun installLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        TimberImplementation.init()
    }

}