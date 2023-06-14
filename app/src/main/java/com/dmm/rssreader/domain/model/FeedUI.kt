package com.dmm.rssreader.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "feeds")
data class FeedUI(
	@ColumnInfo(name = "feed_source")
	val feedSource: String = "",
	@ColumnInfo(name = "description")
	val description: String = "",
	@ColumnInfo(name = "link")
	val link: String? = null,
	@ColumnInfo(name = "image")
	val image: String = "",
	@ColumnInfo(name = "published")
	val published: String = "",
	@ColumnInfo(name = "favourite")
	var favourite: Boolean = false
): Parcelable, BaseModel()
