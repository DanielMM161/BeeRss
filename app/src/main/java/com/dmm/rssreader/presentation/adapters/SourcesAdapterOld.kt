package com.dmm.rssreader.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.dmm.rssreader.databinding.ItemSourcesBinding
import com.dmm.rssreader.domain.model.Source

class SourcesAdapterOld(
	private val sources: List<Source>,
	private val userFeeds: List<Source>,
	private val onCheckedChangeListener: ((Source) -> Unit)
) : BaseAdapter() {

	private lateinit var binding: ItemSourcesBinding

	override fun getCount(): Int {
		return sources.size
	}

	override fun getItem(pos: Int): Source {
		return sources[pos]
	}

	override fun getItemId(pos: Int): Long {
		return pos.toLong()
	}

	override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
		binding = ItemSourcesBinding.inflate(
			LayoutInflater.from(parent!!.context),
			parent,
			false
		)
		val item = getItem(pos)
		binding.source = item
		binding.titleSource.text = item.title

		// Auto Selected Source
		if(userFeeds.contains(item)) {
			binding.switchSource.isChecked = true
		}

		binding.switchSource.setOnCheckedChangeListener { _, _ ->
			onCheckedChangeListener.invoke(item)
		}

		return binding.root
	}
}