package com.dmm.rssreader.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
	val id: Int = 1,
	var fullName: String = "",
	var userName: String = "",
	var email: String = "",
	var photoUrl: String = "",
	var theme: String = "",
	var sources: @RawValue MutableList<Source> = mutableListOf(),
	val isAuthenticated: Boolean = false,
	val userUid: String = "",
	val favouritesFeeds: @RawValue MutableList<FeedUI> = mutableListOf()
): Parcelable
