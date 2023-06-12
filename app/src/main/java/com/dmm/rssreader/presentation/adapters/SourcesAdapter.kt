package com.dmm.rssreader.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dmm.rssreader.databinding.ItemSourcesBinding
import com.dmm.rssreader.domain.model.Source

class SourcesAdapter(
	private val sources: List<Source>,
	private val userFeeds: List<Source>,
	private val onCheckedChangeListener: ((Source) -> Unit)
) : GenericBaseAdapter<Source, ItemSourcesBinding>(
	sources,
	ItemSourcesBinding::inflate
) {

	override fun getView(item: Source) {
		binding.source = item
		binding.titleSource.text = item.title

		// Auto Selected Source
		if(userFeeds.contains(item)) {
			binding.switchSource.isChecked = true
		}

		binding.switchSource.setOnCheckedChangeListener { _, _ ->
			onCheckedChangeListener.invoke(item)
		}
		super.getView(item)
	}
}