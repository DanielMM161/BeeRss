package com.dmm.rssreader.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.dmm.rssreader.databinding.ItemSourcesBinding
import com.dmm.rssreader.domain.model.SingleSources

class SourcesAdapter(
	private val sourceList: List<SingleSources>,
	private val userFeeds: List<String>,
	private val onCheckedChangeListener: ((String, Boolean) -> Unit)
	) : BaseAdapter() {

	private lateinit var binding: ItemSourcesBinding

	override fun getCount(): Int {
		return sourceList.size
	}

	override fun getItem(pos: Int): SingleSources {
		return sourceList[pos]
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
		binding.titleSource.text = item.title
		binding.imageSoruce.setImageResource(item.imageRes)
		// Auto Selected Source
		if(userFeeds.contains(item.title)) {
			binding.switchSource.isChecked = true
		}
		binding.switchSource.setOnCheckedChangeListener { _, isChecked ->
			onCheckedChangeListener.invoke(item.title, isChecked)
		}

		return binding.root
	}
}