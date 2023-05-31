package com.dmm.rssreader.data.network.apis

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
	@GET("{path}")
	fun fetchData(@Path(value = "path", encoded = true) path: String): Call<String>
}