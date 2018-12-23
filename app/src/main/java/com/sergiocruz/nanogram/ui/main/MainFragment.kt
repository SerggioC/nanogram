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
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sergiocruz.nanogram.R
import com.sergiocruz.nanogram.adapter.ImagesAdapter
import com.sergiocruz.nanogram.model.ImageVar
import com.sergiocruz.nanogram.util.InfoLevel.WARNING
import com.sergiocruz.nanogram.util.hasSavedToken
import com.sergiocruz.nanogram.util.showToast
import kotlinx.android.synthetic.main.main_fragment.view.*

class MainFragment : Fragment(), ImagesAdapter.ImageClickListener {


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        initializeRecyclerView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (hasSavedToken(this.context!!)) {
            viewModel.getUserMedia(this.context!!).observe(this, Observer { response ->
                Log.i("Sergio> ", "response: $response")
                val data = response.data

                val imageList =
                    (0 until data!!.size)
                        .filter { data[it].type == "image" }
                        .map { index ->
                            ImageVar(
                                data[index]?.images!!,
                                data[index]?.likes,
                                data[index]?.comments,
                                data[index]?.caption
                            )
                        } as MutableList
                imagesAdapter.swapData(imageList)

                data.forEach {
                    if (it.type == "carousel") {
                        val images =
                            List(it.carouselMedia!!.size) { index ->
                                ImageVar(
                                    it.carouselMedia!![index].images!!,
                                    it.likes,
                                    it.comments,
                                    it.caption
                                )
                            }
                        imageList.addAll(images)
                        imagesAdapter.swapData(imageList)
                    }
                }


            })
        } else {
            goToLoginActivity()
        }

    }

    private lateinit var imagesAdapter: ImagesAdapter

    private fun initializeRecyclerView(view: View) {
        imagesAdapter = ImagesAdapter(this)
        view.images_recyclerview?.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        view.images_recyclerview?.setHasFixedSize(false)
        view.images_recyclerview?.adapter = imagesAdapter
    }

    override fun onImageClicked(image: ImageVar?) {
        gotoImageDetails(image)
    }

    private fun gotoImageDetails(image: ImageVar?) {
        showToast(this.context!!, "Not done yet ${image?.caption?.text}", WARNING)
    }

    private fun goToLoginActivity() {
        activity?.finish()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

}
