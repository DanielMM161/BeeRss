package com.dmm.rssreader.domain.extension

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.set

fun View.show() {
	this.visibility = View.VISIBLE
}

fun View.gone() {
	this.visibility = View.GONE
}

fun TextView.Error(color: Int, errorText: String) {
	this.text = errorText
	this.setTextColor(color)
}