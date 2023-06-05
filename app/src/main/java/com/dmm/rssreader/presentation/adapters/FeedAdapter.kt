package com.dmm.rssreader.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ItemFeedBinding
import com.dmm.rssreader.domain.model.FeedUI

class FeedAdapter() : RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder>() {

	inner class FeedAdapterViewHolder(private val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(feedUI: FeedUI) {
			binding.feed = feedUI
			setImageResourceImageButton(binding, feedUI.favourite)

			binding.share.setOnClickListener {
				shareClickListener?.let {
					it(listOf(feedUI.link ?: "", feedUI.feedSource, feedUI.title))
				}
			}

			binding.save.setOnClickListener {
				setImageResourceImageButton(binding, !feedUI.favourite)
				readLaterOnItemClickListener?.let { it(feedUI) }
			}
		}
	}

	private val diffCallback = object: DiffUtil.ItemCallback<FeedUI>() {
		override fun areItemsTheSame(oldItem: FeedUI, newItem: FeedUI): Boolean {
			return oldItem.title == newItem.title
		}

		override fun areContentsTheSame(oldItem: FeedUI, newItem: FeedUI): Boolean {
			return oldItem == newItem
		}
	}

	val differ = AsyncListDiffer(this, diffCallback)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapterViewHolder {
		val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context))
		return FeedAdapterViewHolder(binding)
	}

	override fun onBindViewHolder(holder: FeedAdapterViewHolder, position: Int) {
		val item = differ.currentList[position]
		holder.itemView.apply {
			setOnClickListener {
				onItemClickListener?.let { it(item) }
			}
		}
		holder.bind(item)
	}

	private var onItemClickListener: ((FeedUI) -> Unit)? = null
	private var readLaterOnItemClickListener: ((FeedUI) -> Unit)? = null
	private var shareClickListener: ((List<String>) -> Unit)? = null

	fun setOnItemClickListener(listener: (FeedUI) -> Unit) {
		onItemClickListener = listener
	}

	fun setReadLaterOnItemClickListener(listener: (FeedUI) -> Unit) {
		readLaterOnItemClickListener = listener
	}

	fun setShareClickListener(listener: (List<String>) -> Unit) {
		shareClickListener = listener
	}

	override fun getItemCount(): Int {
		return differ.currentList.size
	}

	private fun setImageResourceImageButton(binding: ItemFeedBinding, favourite: Boolean) {
		if(favourite) {
			binding.save.setImageResource(R.drawable.bookmark_add_fill)
		} else {
			binding.save.setImageResource(R.drawable.bookmark_add)
		}
	}
}