package com.dmm.rssreader.data.local

import com.dmm.rssreader.R
import com.dmm.rssreader.domain.model.SingleSources
import com.dmm.rssreader.utils.Constants.SOURCE_ANDROID_BLOGS
import com.dmm.rssreader.utils.Constants.SOURCE_ANDROID_MEDIUM
import com.dmm.rssreader.utils.Constants.SOURCE_DANLEW_BLOG
import com.dmm.rssreader.utils.Constants.SOURCE_DEVELOPER_CO
import com.dmm.rssreader.utils.Constants.SOURCE_KOTLIN_WEEKLY

object ContentResources {
	val contentResources = listOf(
		SingleSources(
			title = SOURCE_ANDROID_BLOGS,
			imageRes = R.drawable.android_blogs
		),
		SingleSources(
			title = SOURCE_ANDROID_MEDIUM,
			imageRes = R.drawable.medium
		),
		SingleSources(
			title = SOURCE_KOTLIN_WEEKLY,
			imageRes = R.drawable.vincent
		),
		SingleSources(
			title = SOURCE_DANLEW_BLOG,
			imageRes = R.drawable.danlew
		),
		SingleSources(
			title = SOURCE_DEVELOPER_CO,
			imageRes = R.drawable.android_co
		)
	)
}