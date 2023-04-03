package com.dmm.rssreader.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.data.persistence.converters.ConverterList

@Database(entities = [FeedUI::class], version = 1, exportSchema = false)
@TypeConverters(value = [ConverterList::class])
abstract class AppDatabase : RoomDatabase() {

  abstract fun feedsDao(): FeedsDao
}