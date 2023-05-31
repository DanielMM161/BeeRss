package com.dmm.rssreader.domain.model

data class Source(
	var id: Int,
	var baseUrl: String,
	val image: String,
	val route: String,
	val title: String,
)  {
	constructor() : this(0,"", "", "", "")
}