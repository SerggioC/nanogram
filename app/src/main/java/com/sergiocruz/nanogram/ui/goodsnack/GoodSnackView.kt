package com.sergiocruz.nanogram.ui.goodsnack

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import com.google.android.material.snackbar.ContentViewCallback
import com.sergiocruz.nanogram.R

class GoodSnackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    private val snackImage: ImageView
    private val snackFrame: TextView
    private val snackText: TextView

    init {
        View.inflate(context, R.layout.good_snack_layout, this)
        clipToPadding = false
        this.snackImage = findViewById(R.id.snack_image)
        this.snackFrame = findViewById(R.id.messageFrame)
        this.snackText = findViewById(R.id.snackMessage)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        performScaleShakingAnim(snackImage, 0f, 2f, 1f, true, snackFrame, 0f, 1.5f, 1f)
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        performScaleShakingAnim(snackImage, 1f, 1.5f, 0f, false, snackFrame, 1f, 1.2f, 0f)
    }

    private fun performScaleShakingAnim(view1: View, f1: Float, f2: Float, f3: Float, shake: Boolean, view2: View, f12: Float, f22: Float, f32: Float) {
        val scaleX = ObjectAnimator.ofFloat(view1, View.SCALE_X, f1, f2, f3)
        val scaleY = ObjectAnimator.ofFloat(view1, View.SCALE_Y, f1, f2, f3)
        val scaleX2 = ObjectAnimator.ofFloat(view2, View.SCALE_X, f12, f22, f32)
        val scaleY2 = ObjectAnimator.ofFloat(view2, View.SCALE_Y, f12, f22, f32)

        val scaleX3 = ObjectAnimator.ofFloat(snackText, View.SCALE_X, f12, f22, f32)
        val scaleY3 = ObjectAnimator.ofFloat(snackText, View.SCALE_Y, f12, f22, f32)

        val shakeAnim = AnimationUtils.loadAnimation(this@GoodSnackView.context, R.anim.vibrate)

        val listAnimations = listOf(scaleX, scaleY, scaleX2, scaleY2,scaleX3, scaleY3)

        val animatorSet = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            duration = 500
            playTogether(listAnimations)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (shake) {
                        this@GoodSnackView.snackImage.startAnimation(shakeAnim)
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
        }
        animatorSet.start()
    }



}