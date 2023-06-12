package com.dmm.rssreader.data.persistence

import androidx.room.*
import com.dmm.rssreader.domain.model.FeedUI

@Dao
interface FeedsDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun saveFavouriteFeed(feedUI: FeedUI)

	@Query("UPDATE feeds SET favourite = :favourite WHERE title = :title")
	suspend fun updateFeed(favourite: Boolean, title: String)

	@Query("DELETE FROM feeds")
	suspend fun deleteTable()

	@Query("DELETE FROM feeds WHERE feed_source = :feedSource")
	suspend fun deleteFeedsByFeedSource(feedSource: String)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFeeds(feedUI: List<FeedUI>)

	@Query("SELECT * FROM feeds WHERE favourite = 1")
	suspend fun getFavouriteFeeds(): List<FeedUI>

	@Query("SELECT * FROM feeds WHERE feed_source = :feedSource")
	suspend fun getFeedsList(feedSource: String): List<FeedUI>

	@Query("SELECT * FROM feeds")
	suspend fun getAllFeeds(): List<FeedUI>
}