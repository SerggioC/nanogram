package com.sergiocruz.nanogram.util

import timber.log.Timber

object TimberImplementation {

    private var debugTree: MyDebugTree? = null

    fun init() {
        // Avoid duplication
        if (debugTree != null) {
            Timber.uprootAll()
        } else {
            debugTree = MyDebugTree()
        }
        Timber.plant(debugTree!!)
    }
}