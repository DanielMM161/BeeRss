package com.dmm.rssreader.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ItemFeedBinding
import com.dmm.rssreader.domain.model.FeedUI

class FeedAdapter(
	private val callbacks: Callbacks
) : RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder>() {

	inner class FeedAdapterViewHolder(private val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(feedUI: FeedUI) {
			binding.feed = feedUI
			setImageResourceImageButton(binding, feedUI.favourite)

			binding.share.setOnClickListener {
				callbacks.shareClickListener(listOf(feedUI.link ?: "", feedUI.feedSource, feedUI.title))
			}

			binding.save.setOnClickListener {
				callbacks.readLaterOnItemClickListener(feedUI)
				setImageResourceImageButton(binding, !feedUI.favourite)
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

	override fun onBindViewHolder(holder: FeedAdapter.FeedAdapterViewHolder, position: Int) {
		val item = differ.currentList[position]
		holder.itemView.apply {
			setOnClickListener {
				callbacks.setOnItemClickListener(item)
			}
		}
		holder.bind(item)
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

	interface Callbacks {
		fun shareClickListener(items: List<String>)
		fun readLaterOnItemClickListener(item: FeedUI)
		fun setOnItemClickListener(item: FeedUI)
	}
}