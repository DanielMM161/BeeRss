package com.dmm.rssreader.presentation.fragments

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ReadLaterFragmentBinding
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.presentation.adapters.FeedAdapter
import com.dmm.rssreader.presentation.dialog.FeedDescriptionDialog
import com.dmm.rssreader.utils.NotificationsUI.Companion.snackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadLaterFragment : BaseFragment<ReadLaterFragmentBinding>(
	ReadLaterFragmentBinding::inflate
), FeedAdapter.Callbacks {

	private lateinit var feedAdapter: FeedAdapter
	private lateinit var readLaterRV: RecyclerView
	private lateinit var totalFeedText: TextView

	override fun onViewCreated() {
		super.onViewCreated()

		readLaterRV = binding.rvFeeds
		totalFeedText = binding.toolbarHome.totalFeeds
		setUpRecyclerView()

		viewModel.getFavouriteFeeds()
		collectFavouriteFeeds()
		deleteItemSwipe()
	}

	private fun collectFavouriteFeeds() {
		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.favouritesFeeds.collect {
				withContext(Dispatchers.Main) {
					totalFeedText.text = it.size.toString()
					binding.noReadLater.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
					binding.willBeHere.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
				}
				feedAdapter.differ.submitList(it)
			}
		}
	}

	private fun handleSnackBar(feed: FeedUI) {
		viewModel.saveFavouriteFeed(feed) {
			viewModel.getFavouriteFeeds()
		}
		val message = getString(R.string.delete_feed, feed.title)
		snackBar(binding.root, message) {
			// On Undo Action
			viewModel.saveFavouriteFeed(feed) {
				viewModel.getFavouriteFeeds()
			}
		}
	}

	private fun setUpRecyclerView() {
		feedAdapter = FeedAdapter(this)
		binding.rvFeeds.adapter = feedAdapter
		binding.rvFeeds.layoutManager = LinearLayoutManager(requireContext())
	}

	private fun deleteItemSwipe() {
		val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
			ItemTouchHelper.UP or ItemTouchHelper.DOWN,
			ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
		) {
			override fun onMove(
				recyclerView: RecyclerView,
				viewHolder: RecyclerView.ViewHolder,
				target: RecyclerView.ViewHolder
			): Boolean {
				return true
			}

			override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
				val position = viewHolder.adapterPosition
				val feed = feedAdapter.differ.currentList[position]
				handleSnackBar(feed)
			}
		}

		ItemTouchHelper(itemTouchHelperCallback).apply {
			attachToRecyclerView(readLaterRV)
		}
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
		handleSnackBar(item)
	}

	override fun setOnItemClickListener(item: FeedUI) {
		viewModel.logSelectItem(item.feedSource)
		FeedDescriptionDialog(item).show(childFragmentManager, "FeedDescriptionDialog")
	}
}