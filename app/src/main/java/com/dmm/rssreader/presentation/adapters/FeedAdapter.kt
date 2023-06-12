package com.dmm.rssreader.presentation.adapters

import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ItemFeedBinding
import com.dmm.rssreader.domain.model.FeedUI

class FeedAdapter() : GenericRecyclerViewAdapter<FeedUI, ItemFeedBinding>(
	ItemFeedBinding::inflate
) {

	override fun bind(item: FeedUI) {
		super.bind(item)
		binding.feed = item
		setImageResourceImageButton(binding, item.favourite)

		binding.share.setOnClickListener {
			shareClickListener?.let {
				it(listOf(item.link ?: "", item.feedSource, item.title))
			}
		}

		binding.save.setOnClickListener {
			setImageResourceImageButton(binding, !item.favourite)
			readLaterOnItemClickListener?.let { it(item) }
		}
	}

	private var readLaterOnItemClickListener: ((FeedUI) -> Unit)? = null
	private var shareClickListener: ((List<String>) -> Unit)? = null

	fun setReadLaterOnItemClickListener(listener: (FeedUI) -> Unit) {
		readLaterOnItemClickListener = listener
	}

	fun setShareClickListener(listener: (List<String>) -> Unit) {
		shareClickListener = listener
	}

	private fun setImageResourceImageButton(binding: ItemFeedBinding, favourite: Boolean) {
		if(favourite) {
			binding.save.setImageResource(R.drawable.bookmark_add_fill)
		} else {
			binding.save.setImageResource(R.drawable.bookmark_add)
		}
	}
}