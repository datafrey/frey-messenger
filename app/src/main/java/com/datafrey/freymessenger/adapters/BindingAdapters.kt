package com.datafrey.freymessenger.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.datafrey.freymessenger.loadImageFromUrl

@BindingAdapter("url")
fun loadImage(view: ImageView, url: String?) {
    url?.let { view.loadImageFromUrl(it) }
}