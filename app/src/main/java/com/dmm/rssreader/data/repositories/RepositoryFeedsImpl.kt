package com.dmm.rssreader.data.repositories

import android.util.Log
import com.dmm.rssreader.data.network.apis.*
import com.dmm.rssreader.data.persistence.FeedsDao
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.domain.repositories.RepositoryFeeds
import com.dmm.rssreader.utils.Constants.USERS_COLLECTION
import com.dmm.rssreader.utils.FeedParser
import com.dmm.rssreader.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepositoryFeedsImpl @Inject constructor(
	private val feedsDao: FeedsDao,
	private val fireStore: FirebaseFirestore,
) : RepositoryFeeds {

	override suspend fun fetchFeeds(baseUrl: String, route: String, sourceTitle: String): Resource<List<FeedUI>?> {
		var result = feedsDao.getFeedsList(sourceTitle)

		if(result.isEmpty()) {
			val retrofit = createRetrofit(baseUrl)
			val apiService = retrofit.create(ApiService::class.java)

			result = handleResponse(apiService.fetchData(route).execute(), sourceTitle)

			setFavouritesFeeds(result)
			saveDataLocal(result)
		}
		setFavouritesFeeds(result)
		return Resource.Success(result)
	}

	private suspend fun setFavouritesFeeds(feeds: List<FeedUI>) {
		val favouriteFeeds = feedsDao.getFavouriteFeeds()
		feeds.forEach {
			it.favourite = favouriteFeeds.contains(it)
		}
	}

	override suspend fun saveDataLocal(feedUI: List<FeedUI>) {
		feedsDao.insertFeeds(feedUI)
	}

	override fun getFavouriteFeeds(): Flow<List<FeedUI>> = flow {
		emit(feedsDao.getFavouriteFeeds())
	}

	override fun updateFavouritesFeedsFireBase(favouriteFeeds: List<FeedUI>, documentPath: String) {
		fireStore.collection(USERS_COLLECTION).document(documentPath).update(mapOf(
			"favouritesFeeds" to favouriteFeeds
		))
	}

	override suspend fun updateFeed(favorite: Boolean, title: String) {
		feedsDao.updateFeed(favorite, title)
	}

	override suspend fun deleteTable() {
		feedsDao.deleteTable()
	}

	override suspend fun deleteFeeds(sourceFeed: String) {
		feedsDao.deleteFeedsByFeedSource(sourceFeed)
	}

	private fun handleResponse(response: Response<String>, source: String): List<FeedUI> {
		try {
			var result: List<FeedUI> = listOf()
			val body = response.body() ?: ""
			Log.e("handleResponse ", "${source} -- ${body.length}")
			if(response.isSuccessful && body.isNotEmpty()) {
				result = FeedParser().parse(body, source)
			}
			return result
		} catch (e: Exception) {
			Log.e("ERROR IN HANDLE RESPONSE --->  ", "${e.message}")
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