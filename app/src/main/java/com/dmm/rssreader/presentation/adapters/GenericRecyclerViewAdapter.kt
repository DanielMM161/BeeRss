package com.dmm.rssreader.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.dmm.rssreader.databinding.ItemFeedBinding
import com.dmm.rssreader.domain.model.BaseModel
import com.dmm.rssreader.domain.model.FeedUI

abstract class GenericRecyclerViewAdapter<T: BaseModel, VB: ViewBinding>(
	private val bindingInflater: (inflater: LayoutInflater) -> VB,
) : RecyclerView.Adapter<GenericRecyclerViewAdapter<T, VB>.GenericAdapterVH>() {

	private lateinit var _binding: VB
	protected val binding: VB get() = _binding
	protected open fun onBind(item: T) = Unit
	private val diffCallback = object: DiffUtil.ItemCallback<T>() {
		override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
			return oldItem.title == newItem.title
		}

		override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
			return oldItem.equals(newItem)
		}
	}
	val differ = AsyncListDiffer(this, diffCallback)

	inner class GenericAdapterVH(private val binding: VB) : RecyclerView.ViewHolder(binding.root) {
		fun bind(item: T) {
			onBind(item)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericAdapterVH {
		_binding = bindingInflater.invoke(LayoutInflater.from(parent.context))
		return GenericAdapterVH(_binding)
	}

	override fun onBindViewHolder(holder: GenericAdapterVH, position: Int) {
		val item = differ.currentList[position]
		holder.bind(item)
	}

	override fun getItemCount(): Int {
		return differ.currentList.size
	}

	interface Callbacks<T> {
		fun onBind(item: T)
	}
}