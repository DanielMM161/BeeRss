package com.dmm.rssreader.domain.repositories

import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RepositoryFeeds {
	suspend fun fetchFeeds(baseUrl: String, route: String, sourceTitle: String): List<FeedUI>
	suspend fun insertFeedLocal(feedUI: List<FeedUI>)
	suspend fun updateFeedLocal(favorite: Boolean, title: String)
	suspend fun getAllFeedsLocal(): List<FeedUI>
	suspend fun deleteTable()
	suspend fun deleteFeedLocal(sourceTitle: String)
	suspend fun getFavouriteFeedsLocal(): List<FeedUI>
}