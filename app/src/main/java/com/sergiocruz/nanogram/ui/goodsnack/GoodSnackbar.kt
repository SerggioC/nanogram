package com.sergiocruz.nanogram.ui.goodsnack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.sergiocruz.nanogram.R

class GoodSnackbar(
    parent: ViewGroup,
    content: GoodSnackView
) : BaseTransientBottomBar<GoodSnackbar>(parent, content, content) {

    init {
        getView().setBackgroundColor(ContextCompat.getColor(view.context, android.R.color.transparent))
        getView().setPadding(0, 0, 0, 0)

    }

    companion object {

        fun make(view: View, duration: Int = LENGTH_LONG, action: (() -> Unit)? = null) {

            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // inflate custom view
            val customView = LayoutInflater.from(view.context).inflate(R.layout.goodsnackview_layout, parent, false) as GoodSnackView

            if (action != null) {
                customView.findViewById<TextView>(R.id.snackAction).visibility = View.VISIBLE
            }

            // Wcreate and show the Snackbar
            val snack = GoodSnackbar(parent, customView).setDuration(duration)
            snack.show()

            customView.findViewById<TextView>(R.id.snackAction).setOnClickListener {
                snack.dismiss()
                action?.invoke()
            }
        }

    }


}