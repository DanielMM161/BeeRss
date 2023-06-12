package com.dmm.rssreader.presentation.adapters

import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ItemSourceDialogBinding
import com.dmm.rssreader.domain.model.Source

class SourceExpandableAdapter(private val groupTitle: List<String>, private val listSource: Map<String, List<Source>>) : GenericExpandableAdapter<Source, ItemSourceDialogBinding>(
	groupTitle,
	listSource,
	R.layout.group_title_source,
	ItemSourceDialogBinding::inflate
) {

	override fun getChildView(item: Source) {
		binding.source = item
		binding.titleSource.text = item.title
		super.getChildView(item)
	}
}