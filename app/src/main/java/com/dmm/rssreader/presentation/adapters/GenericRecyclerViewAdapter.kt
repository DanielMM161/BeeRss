package com.dmm.rssreader.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.dmm.rssreader.domain.model.BaseModel

abstract class GenericRecyclerViewAdapter<T: BaseModel, VB: ViewBinding>(
	private val bindingInflater: (inflater: LayoutInflater) -> VB
) : RecyclerView.Adapter<GenericRecyclerViewAdapter<T, VB>.GenericAdapterVH>() {

	private lateinit var _binding: VB
	protected val binding: VB get() = _binding
	protected open fun bind(item: T) = Unit
	private var onItemClickListener: ((T) -> Unit)? = null
	private val diffCallback = object: DiffUtil.ItemCallback<T>() {
		override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
			return oldItem.id == newItem.id
		}

		override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
			return oldItem.equals(newItem)
		}
	}
	val differ = AsyncListDiffer(this, diffCallback)
	inner class GenericAdapterVH() : RecyclerView.ViewHolder(binding.root) {}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericAdapterVH {
		_binding = bindingInflater.invoke(LayoutInflater.from(parent.context))
		return GenericAdapterVH()
	}

	override fun onBindViewHolder(holder: GenericAdapterVH, position: Int) {
		val item = differ.currentList[position]
		holder.itemView.apply {
			setOnClickListener {
				onItemClickListener?.let { it(item) }
			}
		}
		bind(item)
	}

	fun setOnItemClickListener(listener: (T) -> Unit) {
		onItemClickListener = listener
	}

	override fun getItemCount(): Int {
		return differ.currentList.size
	}
}