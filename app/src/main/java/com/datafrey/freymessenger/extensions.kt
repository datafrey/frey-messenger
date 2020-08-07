package com.datafrey.freymessenger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

val EditText.data get() = text.toString().trim()

inline fun <reified A:Activity> Context.startActivity(intent: Intent.() -> Unit = {}) {
    startActivity(Intent(this, A::class.java).apply(intent))
}

inline fun <reified A:Activity> Fragment.startActivity(intent: Intent.() -> Unit = {}) {
    startActivity(Intent(requireContext(), A::class.java).apply(intent))
}

fun Context.loadImage(url: String, to: ImageView) =
    Glide.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(to)