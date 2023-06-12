package com.dmm.rssreader.domain.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey


abstract class BaseModel(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	var id: Int = 0
	)