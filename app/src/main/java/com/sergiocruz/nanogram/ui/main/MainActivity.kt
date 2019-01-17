package com.sergiocruz.nanogram.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.allPermissionsGranted
import com.sergiocruz.nanogram.getRuntimePermissions
import com.sergiocruz.nanogram.util.TimberImplementation

class MainActivity : AppCompatActivity() {

    companion object {
        var currentPosition: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            TimberImplementation.init()

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GridFragment())
                .commitNow()
            if (!allPermissionsGranted(this)) getRuntimePermissions(this)
        } else {
            // Return here to prevent adding additional
            // Fragments when changing orientation.
            return
        }
    }

}
