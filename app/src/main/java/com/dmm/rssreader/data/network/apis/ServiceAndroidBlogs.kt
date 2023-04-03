package com.dmm.rssreader.data.network.apis

import retrofit2.Response
import retrofit2.http.GET

interface ServiceAndroidBlogs {
	@GET("blogspot/hsDu")
	suspend fun fetchData() : Response<String>
}