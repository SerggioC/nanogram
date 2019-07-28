package com.sergiocruz.nanogram.ui.main

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.sergiocruz.nanogram.App
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.allPermissionsGranted
import com.sergiocruz.nanogram.getRuntimePermissions
import com.sergiocruz.nanogram.ui.goodsnack.GoodSnackbar
import com.sergiocruz.nanogram.util.deleteToken
import com.sergiocruz.nanogram.util.hasSavedToken
import kotlinx.android.synthetic.main.grid_fragment.*
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.popup_window_settings.view.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appe: App

    companion object {
        var currentPosition: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            if (hasSavedToken(this)) {
                goToGridFragment()
            } else {
                goToLoginFragment()
            }

            if (!allPermissionsGranted(this)) getRuntimePermissions(this)
        } else {
            // Return here to prevent adding additional
            // Fragments when changing orientation.
            return
        }
    }

    private fun goToGridFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, GridFragment())
            .commit()
    }

    private fun goToLoginFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, LoginFragment())
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> openSettingsPopup()
            R.id.action_snack -> showSnack()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSnack() {
        GoodSnackbar.make(images_recyclerview, Snackbar.LENGTH_INDEFINITE) {
            Toast.makeText(this, "cenas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSettingsPopup() {
        // inflate the layout of the popup window
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_window_settings, null, true)


        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupView.isFocusable = true // let taps outside the popup also dismiss it
        popupWindow.isOutsideTouchable = true

        popupWindow.animationStyle = R.style.PopupWindowAnimationStyle

        // https://material.io/design/environment/elevation.html#default-elevations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = resources.getDimension(R.dimen.popup_window_elevation)
        }

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(container, Gravity.CENTER, 0, 0)

        if (hasSavedToken(this)) {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
                .getUserInfo().observe(this, Observer {
                    popupView.status.append(getString(R.string.currently_logged) + " " + it.fullName)
                })
        }

        popupView.logout.setOnClickListener {
            popupWindow.dismiss()
            deleteToken(this)
            goToLoginFragment()
        }

    }


}
