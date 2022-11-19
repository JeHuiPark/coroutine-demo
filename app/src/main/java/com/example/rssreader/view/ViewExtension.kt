@file:OptIn(DelicateCoroutinesApi::class)

package com.example.rssreader.view

import android.view.View
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun View.show() {
    GlobalScope.launch(Dispatchers.Main) {
        this@show.visibility = View.VISIBLE
    }
}

fun View.hide() {
    GlobalScope.launch(Dispatchers.Main) {
        this@hide.visibility = View.GONE
    }
}
