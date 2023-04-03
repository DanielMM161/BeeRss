package com.dmm.rssreader.data.network.apis

import retrofit2.Response
import retrofit2.http.GET

interface ServiceDanLew {

	@GET("rss/")
	suspend fun fetchData(): Response<String>
}