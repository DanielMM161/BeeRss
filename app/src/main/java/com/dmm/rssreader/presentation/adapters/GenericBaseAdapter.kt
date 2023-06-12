package com.dmm.rssreader.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

abstract class GenericBaseAdapter<T, VB: ViewBinding>(
	private val items: Collection<T>,
	private val bindingInflater: (inflater: LayoutInflater) -> VB
) : BaseAdapter() {

	private lateinit var _binding: VB
	protected val binding: VB get() = _binding
	protected open fun getView(item: T) = Unit

	override fun getCount(): Int {
		return items.size
	}

	override fun getItem(pos: Int): T {
		return items.elementAt(pos)
	}

	override fun getItemId(pos: Int): Long {
		return pos.toLong()
	}

	override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
		_binding = if (convertView == null) {
			bindingInflater.invoke(LayoutInflater.from(parent?.context))
		} else {
			DataBindingUtil.getBinding(convertView)!!
		}
		getView(getItem(pos))
		return binding.root
	}
}