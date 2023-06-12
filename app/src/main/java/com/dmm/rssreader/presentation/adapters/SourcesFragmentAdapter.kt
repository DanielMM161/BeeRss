package com.dmm.rssreader.presentation.adapters

import android.widget.ImageView
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ItemSourceFragmentBinding
import com.dmm.rssreader.domain.model.Source

class SourcesFragmentAdapter(
	private val sources: List<Source>,
	private val userFeeds: List<Source>,
	private val updateFollowSource: (item: Source) -> Unit
) : GenericBaseAdapter<Source, ItemSourceFragmentBinding>(
	sources,
	ItemSourceFragmentBinding::inflate
) {

	override fun getView(item: Source) {

		binding.source = item
		binding.titleSource.text = item.title

		setIconFollowSource(userFeeds.contains(item))

		binding.imageAdd.setOnClickListener {
			setIconFollowSource(!userFeeds.contains(item))
			updateFollowSource(item)
		}
		super.getView(item)
	}

	fun setIconFollowSource(follow: Boolean) {
		if (follow) {
			binding.imageAdd.setImageResource(R.drawable.ic_baseline_check_24)
		} else {
			binding.imageAdd.setImageResource(R.drawable.ic_baseline_add_24)
		}
	}
}