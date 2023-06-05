package com.dmm.rssreader.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Source(
	var id: Int,
	var baseUrl: String,
	val image: String,
	val route: String,
	val title: String,
): Parcelable {
	constructor() : this(0,"", "", "", "")
}