package com.dmm.rssreader.data.network.apis

import retrofit2.Response
import retrofit2.http.GET

interface ServiceKotlinWeekly {

	@GET("rss.xml")
	suspend fun fetchData(): Response<String>
}