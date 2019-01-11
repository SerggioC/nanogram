package com.sergiocruz.nanogram.util

import timber.log.Timber

class MyDebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return "Sergio> ${super.createStackElementTag(element)}; Method ${element.methodName}; Line ${element.lineNumber}"
    }

}