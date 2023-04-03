package com.dmm.rssreader.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class Resource<T>(
	val data: T? = null,
	@StringRes open val resId: Int? = null
) {
	class Success<T>(data: T? = null, @StringRes override val resId: Int? = null) : Resource<T>(data)
	class ErrorCaught<T>(data: T? = null , @StringRes override val resId: Int) : Resource<T>(data, resId)
	class Loading<T> : Resource<T>()
	data class Error<T>(val message: String): Resource<T>()


	fun asString(context: Context?): String {
		if (context != null) {
			return when(this) {
				is Error -> message
				is ErrorCaught -> context.getString(resId)
				else -> ""
			}
		}
		return ""
	}
}
