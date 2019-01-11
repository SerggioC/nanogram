package com.sergiocruz.nanogram.util

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.util.InfoLevel.*
import timber.log.Timber

enum class InfoLevel {
    INFO, CONFIRM, WARNING, ERROR
}

//fun showCustomToast(context: Context, toastText: String, icon_RID: Int, text_color_Res_Id: Int, duration: Int? = Toast.LENGTH_LONG) {
fun showToast(
    context: Context?,
    toastText: String,
    level: InfoLevel,
    duration: Int = Toast.LENGTH_LONG
) {
    val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    if (inflater == null) {
        Toast.makeText(context, toastText, duration).show()
        return
    }
    val layout = inflater.inflate(R.layout.custom_toast, null)
    val text: TextView = layout.findViewById(R.id.toast_layout_text)
    text.text = toastText
    val textColorResId = when (level) {
        INFO -> android.R.color.holo_blue_dark
        CONFIRM -> android.R.color.holo_green_light
        WARNING -> android.R.color.holo_orange_dark
        ERROR -> android.R.color.holo_red_dark
    }
    val iconResId = when (level) {
        INFO -> R.mipmap.ic_info
        CONFIRM -> R.mipmap.ic_ok
        WARNING -> R.mipmap.ic_warn
        ERROR -> R.mipmap.ic_error
    }
    text.setTextColor(ContextCompat.getColor(context, textColorResId))
    val imageV: ImageView = layout.findViewById(R.id.toast_img)
    imageV.setImageResource(iconResId)
    val theCustomToast = Toast(context)
    theCustomToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
    theCustomToast.duration = duration
    theCustomToast.view = layout
    theCustomToast.show()
}

fun animateItemViewSlideFromBottom(viewToAnimate: View, timeOffSet: Long) {
    val bottomAnimation = AnimationUtils.loadAnimation(
        viewToAnimate.context,
        R.anim.item_animation_slide_from_bottom
    )
    bottomAnimation.startOffset = timeOffSet
    viewToAnimate.startAnimation(bottomAnimation)
}


fun enterFullScreen(activity: Activity?) {
    Timber.d("Entering FullScreen")
    val decorView = activity?.window?.decorView
    decorView?.findViewById<Toolbar>(R.id.action_bar)?.visibility = View.GONE
    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
}

fun exitFullScreen(activity: Activity?) {
    Timber.d("Exiting FullScreen")
    val decorView = activity?.window?.decorView
    decorView?.findViewById<Toolbar>(R.id.action_bar)?.visibility = View.VISIBLE
    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}