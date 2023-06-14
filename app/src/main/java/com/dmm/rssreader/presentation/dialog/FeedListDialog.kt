package com.dmm.rssreader.presentation.dialog

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.FeedListDialogBinding
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.domain.model.Source
import com.dmm.rssreader.presentation.adapters.FeedAdapter
import kotlinx.coroutines.launch

class FeedListDialog(private val sourceSelected: Source) : CustomDialog<FeedListDialogBinding>(
	FeedListDialogBinding::inflate,
	R.style.FullScreenDialog
), FeedAdapter.Callbacks {

	private lateinit var feedAdapter: FeedAdapter

	override fun onViewCreated() {
		super.onViewCreated()
		setUpRecyclerView()
		fetchFeeds()

		binding.feedTitle.text = sourceSelected.title
		binding.arrowBack.setOnClickListener {
			dismiss()
		}
	}

	private fun fetchFeeds() {
		lifecycleScope.launch {
			binding.swipeRefresh.isRefreshing = true
			val result = viewModel.fetchFeed(sourceSelected)
			if (result.isNotEmpty()) {
				binding.swipeRefresh.isRefreshing = false
				binding.totalFeeds.text = result.count().toString()

				feedAdapter.differ.submitList(result)
				return@launch
			}
		}
	}

	private fun setUpRecyclerView() {
		feedAdapter = FeedAdapter(this)
		binding.rvFeed.adapter = feedAdapter
		binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
	}

	override fun shareClickListener(items: List<String>) {
		items[0].let {
			viewModel.logShare(items[1], items[2])
			val sendIntent: Intent = Intent().apply {
				action = Intent.ACTION_SEND
				putExtra(Intent.EXTRA_TEXT, it)
				type = "text/plain"
			}

			val shareIntent = Intent.createChooser(sendIntent, null)
			startActivity(shareIntent)
		}
	}

	override fun readLaterOnItemClickListener(item: FeedUI) {
		viewModel.saveFavouriteFeed(item)
	}

	override fun setOnItemClickListener(item: FeedUI) {
		viewModel.logSelectItem(item.feedSource)
		FeedDescriptionDialog(item).show(childFragmentManager, "FeedDescriptionDialog")
	}
}