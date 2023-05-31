package com.dmm.rssreader.data.repositories

import android.provider.SyncStateContract.Helpers.update
import com.dmm.rssreader.data.network.apis.*
import com.dmm.rssreader.data.persistence.FeedsDao
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.domain.repositories.RepositoryFeeds
import com.dmm.rssreader.utils.Constants.USERS_COLLECTION
import com.dmm.rssreader.utils.FeedParser
import com.dmm.rssreader.utils.HostSelectionInterceptor
import com.dmm.rssreader.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import retrofit2.Response
import retrofit2.awaitResponse
import javax.inject.Inject

class RepositoryFeedsImpl @Inject constructor(
	private val feedsDao: FeedsDao,
	private val fireStore: FirebaseFirestore,
	private val apiService: ApiService,
	private val urlInterceptor: HostSelectionInterceptor,
) : RepositoryFeeds {

	override suspend fun fetchFeeds(baseUrl: String, route: String, sourceTitle: String): Resource<List<FeedUI>?> {
		var result = feedsDao.getFeedsList(sourceTitle)

		if(result.isEmpty()) {
			urlInterceptor.setDynamicUrl(baseUrl)
			result = handleResponse(apiService.fetchData(route).awaitResponse(), sourceTitle)

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
		var result: List<FeedUI> = listOf()
		if(response.isSuccessful) {
			result = FeedParser().parse(response.body()!!, source)
		}
		return result
	}
}