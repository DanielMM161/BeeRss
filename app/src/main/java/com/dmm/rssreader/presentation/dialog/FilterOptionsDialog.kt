package com.dmm.rssreader.presentation.dialog

import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.FilterFeedDialogBinding
import com.dmm.rssreader.domain.model.Source
import com.dmm.rssreader.presentation.adapters.SourceExpandableAdapter

class FilterOptionsDialog() : CustomDialog<FilterFeedDialogBinding>(
	FilterFeedDialogBinding::inflate,
	R.layout.filter_feed_dialog,
	R.style.FullScreenDialog
) {

	override fun onViewCreated() {
		super.onViewCreated()
		setUpExpandableAdapter()
	}

	private fun setUpExpandableAdapter() {
		val title = listOf<String>("TITLE ONE")
		val mapSource = mutableMapOf<String, List<Source>>()
		mapSource[title[0]] = viewModel.sources
		binding.expandableSources.setAdapter(SourceExpandableAdapter(title, mapSource))

	}


}