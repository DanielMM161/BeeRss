package com.dmm.rssreader.presentation.adapters

import android.widget.ImageView
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ItemSourceFragmentBinding
import com.dmm.rssreader.domain.model.Source

class SourcesFragmentAdapter(
	sources: List<Source>,
	private val userFeeds: List<Source>,
	private val callbacks: Callbacks
) : GenericBaseAdapter<Source, ItemSourceFragmentBinding>(
	sources,
	ItemSourceFragmentBinding::inflate
) {

	override fun getView(item: Source) {
		super.getView(item)
		binding.source = item
		binding.titleSource.text = item.title

		setIconFollowSource(userFeeds.contains(item), binding.imageAdd)

		binding.sourcesLayout.setOnClickListener {
			callbacks.onItemClick(item)
		}

		binding.imageAdd.apply {
			setOnClickListener {
				setIconFollowSource(!userFeeds.contains(item), this)
				callbacks.onFollowClick(item)
			}
		}
	}

	fun setIconFollowSource(follow: Boolean, imageView: ImageView) {
		if (follow) {
			imageView.setImageResource(R.drawable.ic_baseline_check_24)
		} else {
			imageView.setImageResource(R.drawable.ic_baseline_add_24)
		}
	}

	interface Callbacks {
		fun onItemClick(item: Source)
		fun onFollowClick(item: Source)
	}
}