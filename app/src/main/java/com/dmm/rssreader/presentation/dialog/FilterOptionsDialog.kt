package com.dmm.rssreader.presentation.dialog

import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.FilterFeedDialogBinding

class FilterOptionsDialog() : CustomDialog<FilterFeedDialogBinding>(
	FilterFeedDialogBinding::inflate,
	R.layout.filter_feed_dialog,
	R.style.FullScreenDialog
) {

	override fun onViewCreated() {
		super.onViewCreated()
		onClickSourceLayout()
	}

	fun onClickSourceLayout() {
		binding.userFeedsLayout.setOnClickListener {
			val sourcesDialogFragment = SourcesDialogFragment()
			sourcesDialogFragment.show(parentFragmentManager, sourcesDialogFragment.tag)
		}
	}
}