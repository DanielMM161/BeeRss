package com.dmm.rssreader.utils

import dagger.Reusable
import okhttp3.Interceptor
import okhttp3.Response
import org.jetbrains.annotations.NotNull
import java.io.IOException


class HostSelectionInterceptor() : Interceptor {
	@Volatile
	private var dynamicUrl = "";

	fun setDynamicUrl(url: String) {
		dynamicUrl = url;
	}
	@NotNull
	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response {
		val originalRequest = chain.request()
		if (dynamicUrl.isNotEmpty()) {
			val modifieRequest = originalRequest.newBuilder()
				.url(dynamicUrl + originalRequest.url)
				.build()
			return chain.proceed(modifieRequest)
		}

		return chain.proceed(originalRequest)
	}
}