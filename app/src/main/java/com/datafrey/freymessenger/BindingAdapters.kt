package com.datafrey.freymessenger

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("url")
fun loadImage(view: ImageView, url: String?) {
    if (url != null) {
        view.context.loadImage(url, view)
    }
}