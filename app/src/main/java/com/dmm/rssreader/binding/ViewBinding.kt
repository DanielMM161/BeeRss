package com.dmm.rssreader.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.dmm.rssreader.R
import com.dmm.rssreader.data.local.ContentResources.contentResources
import com.dmm.rssreader.domain.model.FeedUI

object ViewBinding {

	@JvmStatic
	@BindingAdapter("loadImage")
	fun bindLoadImage(view: ImageView, url: String) {
		var errorImage: Int = R.drawable.ic_baseline_broken_image_24
		val context = view.context
		Glide.with(context)
			.load(url)
			.placeholder(R.drawable.loading_animation)
			.error(errorImage)
			.centerCrop()
			.into(view)
	}
}