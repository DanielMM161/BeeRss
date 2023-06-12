package com.dmm.rssreader.presentation.adapters

import com.dmm.rssreader.databinding.ItemSourceDialogBinding
import com.dmm.rssreader.domain.model.Source

class SourcesDialogAdapter(
	private val sources: List<Source>,
	private val userFeeds: List<Source>,
	private val onCheckedChangeListener: (item: Source) -> Unit
) : GenericBaseAdapter<Source, ItemSourceDialogBinding>(
	sources,
	ItemSourceDialogBinding::inflate
) {

	override fun getView(item: Source) {
		binding.source = item
		binding.titleSource.text = item.title

		// Auto Selected Source
		if(userFeeds.contains(item)) {
			binding.switchSource.isChecked = true
		}

		binding.switchSource.setOnCheckedChangeListener { _, selected ->
			onCheckedChangeListener(item)
		}
		super.getView(item)
	}
}