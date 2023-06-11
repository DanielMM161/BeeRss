package com.dmm.rssreader.presentation.adapters

import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ItemSourcesBinding
import com.dmm.rssreader.domain.model.Source

class SourceExpandableAdapter(private val groupTitle: List<String>, private val listSource: Map<String, List<Source>>) : GenericExpandableAdapter<Source, ItemSourcesBinding>(
	groupTitle,
	listSource,
	R.layout.group_title_source,
	ItemSourcesBinding::inflate
) {

	override fun getChildView(item: Source) {
		binding.source = item
		binding.titleSource.text = item.title
		super.getChildView(item)
	}
}