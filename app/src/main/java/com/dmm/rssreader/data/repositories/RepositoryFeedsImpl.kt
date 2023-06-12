package com.dmm.rssreader.data.repositories

import com.dmm.rssreader.data.network.apis.*
import com.dmm.rssreader.data.persistence.FeedsDao
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.domain.repositories.RepositoryFeeds
import com.dmm.rssreader.utils.FeedParser
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepositoryFeedsImpl @Inject constructor(
	private val feedsDao: FeedsDao
) : RepositoryFeeds {

	override suspend fun fetchFeeds(baseUrl: String, route: String, sourceTitle: String): List<FeedUI> {
		var result = feedsDao.getFeedsList(sourceTitle)

		if(result.isEmpty()) {
			val retrofit = createRetrofit(baseUrl)
			val apiService = retrofit.create(ApiService::class.java)

			result = handleResponse(apiService.fetchData(route).awaitResponse(), sourceTitle)
			insertFeedLocal(result)
		}
		setFavouritesFeeds(result)
		return result
	}

	private suspend fun setFavouritesFeeds(feeds: List<FeedUI>) {
		val favouriteFeeds = feedsDao.getFavouriteFeeds()
		feeds.forEach {
			it.favourite = favouriteFeeds.contains(it)
		}
	}

	override suspend fun getAllFeedsLocal(): List<FeedUI> {
		return feedsDao.getAllFeeds()
	}

	override suspend fun insertFeedLocal(feedUI: List<FeedUI>) {
		feedsDao.insertFeeds(feedUI)
	}

	override suspend fun getFavouriteFeedsLocal(): List<FeedUI> {
		return feedsDao.getFavouriteFeeds()
	}

	override suspend fun updateFeedLocal(favorite: Boolean, title: String) {
		feedsDao.updateFeed(favorite, title)
		getFavouriteFeedsLocal()
	}

	override suspend fun deleteTable() {
		feedsDao.deleteTable()
	}

	override suspend fun deleteFeedLocal(sourceFeed: String) {
		feedsDao.deleteFeedsByFeedSource(sourceFeed)
	}

	private fun handleResponse(response: Response<String>, source: String): List<FeedUI> {
		try {
			var result: List<FeedUI> = listOf()
			val body = response.body() ?: ""
			if(response.isSuccessful && body.isNotEmpty()) {
				result = FeedParser().parse(body, source)
			}
			return result
		} catch (e: Exception) {
			return listOf()
		}
	}

	private fun createRetrofit(baseUrl: String): Retrofit {
		var okHttpClient = OkHttpClient().newBuilder()
			.retryOnConnectionFailure(true)
			.followRedirects(true)
			.followSslRedirects(true)
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.build()

		return Retrofit.Builder()
			.baseUrl(baseUrl)
			.client(okHttpClient)
			.addConverterFactory(ScalarsConverterFactory.create())
			.build()
	}
}