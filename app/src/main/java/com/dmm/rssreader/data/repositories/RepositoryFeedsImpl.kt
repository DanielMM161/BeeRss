package com.dmm.rssreader.data.repositories

import android.util.Log
import com.dmm.rssreader.data.network.apis.*
import com.dmm.rssreader.data.persistence.FeedsDao
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.domain.repositories.RepositoryFeeds
import com.dmm.rssreader.utils.Constants
import com.dmm.rssreader.utils.Constants.SOURCE_ANDROID_BLOGS
import com.dmm.rssreader.utils.Constants.SOURCE_ANDROID_MEDIUM
import com.dmm.rssreader.utils.Constants.SOURCE_DANLEW_BLOG
import com.dmm.rssreader.utils.Constants.SOURCE_DEVELOPER_CO
import com.dmm.rssreader.utils.Constants.SOURCE_KOTLIN_WEEKLY
import com.dmm.rssreader.utils.FeedParser
import com.dmm.rssreader.utils.Resource
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.*
import retrofit2.Response
import javax.inject.Inject

class RepositoryFeedsImpl @Inject constructor(
	private val feedsDao: FeedsDao,
	private val docRef: CollectionReference,
	private val serviceAndroidBlogs: ServiceAndroidBlogs,
	private val serviceAndroidDevelopers: ServiceAndroidDevelopers,
	private val serviceDanLew: ServiceDanLew,
	private val serviceDevsMedium: ServiceDevsMedium,
	private val serviceKotlinWeekly: ServiceKotlinWeekly,
) : RepositoryFeeds {

	override suspend fun fetchFeeds(source: String): Resource<List<FeedUI>?> {
		var result = feedsDao.getFeedsList(source)
		if(result.isEmpty()) {
			when(source) {
				SOURCE_ANDROID_BLOGS -> result = handleResponse(serviceAndroidBlogs.fetchData(), source)
				SOURCE_ANDROID_MEDIUM -> result = handleResponse(serviceDevsMedium.fetchData(), source)
				SOURCE_DEVELOPER_CO -> result = handleResponse(serviceAndroidDevelopers.fetchData(), source)
				SOURCE_DANLEW_BLOG -> result = handleResponse(serviceDanLew.fetchData(), source)
				SOURCE_KOTLIN_WEEKLY -> result = handleResponse(serviceKotlinWeekly.fetchData(), source)
			}
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
		docRef.document(documentPath).update(mapOf(
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