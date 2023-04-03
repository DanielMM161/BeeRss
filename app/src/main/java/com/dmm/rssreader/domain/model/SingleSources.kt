package com.dmm.rssreader.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SingleSources(
	val title: String,
	@DrawableRes var imageRes: Int
)
