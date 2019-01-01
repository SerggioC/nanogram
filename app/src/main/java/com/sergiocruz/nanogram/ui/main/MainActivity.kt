package com.sergiocruz.nanogram.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.allPermissionsGranted
import com.sergiocruz.nanogram.getRuntimePermissions

class MainActivity : AppCompatActivity() {

    public companion object {
        public var currentPosition: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        if (!allPermissionsGranted(this)) getRuntimePermissions(this)
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            fragmentManager.popBackStack()
        }
    }
}
