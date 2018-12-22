package com.sergiocruz.nanogram.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.util.hasSavedToken

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        GridLayoutManager

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (hasSavedToken(this.context!!)) {
            viewModel.getUserMedia(this.context!!).observe(this, Observer {
                Log.i("Sergio> ", "it: $it")
                var response = it.data

            })
        } else {
            goToLoginActivity()
        }

    }

    private fun goToLoginActivity() {
        activity?.finish()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

}
