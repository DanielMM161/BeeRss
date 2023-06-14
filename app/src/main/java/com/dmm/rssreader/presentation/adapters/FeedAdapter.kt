package com.dmm.rssreader.presentation.adapters

import android.widget.ImageView
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ItemFeedBinding
import com.dmm.rssreader.domain.model.FeedUI

class FeedAdapter(
	private val callbacks: Callbacks
) : GenericRecyclerViewAdapter<FeedUI, ItemFeedBinding>(
	ItemFeedBinding::inflate
) {

	override fun bind(item: FeedUI) {
		super.bind(item)
		binding.feed = item
		setImageResourceImageButton(binding.save, item.favourite)

		binding.cardLayout.setOnClickListener {
			callbacks.setOnItemClickListener(item)
		}

		binding.share.setOnClickListener {
			callbacks.shareClickListener(listOf(item.link ?: "", item.feedSource, item.title))
		}

		binding.save.apply {
			setOnClickListener {
				setImageResourceImageButton(this, !item.favourite)
				callbacks.readLaterOnItemClickListener(item)
			}
		}
	}

	private fun setImageResourceImageButton(imageView: ImageView, favourite: Boolean) {
		if(favourite) {
			imageView.setImageResource(R.drawable.bookmark_add_fill)
		} else {
			imageView.setImageResource(R.drawable.bookmark_add)
		}
	}

	interface Callbacks {
		fun shareClickListener(items: List<String>)
		fun readLaterOnItemClickListener(item: FeedUI)
		fun setOnItemClickListener(item: FeedUI)
	}
}