package com.dmm.rssreader.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

abstract class BaseModel(
	@PrimaryKey()
	@ColumnInfo(name = "title")
	var title: String = "",
	)