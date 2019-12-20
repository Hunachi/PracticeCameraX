package io.github.hunachi.practicecamerax

import android.content.Context
import android.widget.Toast

fun Context.toast(message: String, option: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, option).show()
}