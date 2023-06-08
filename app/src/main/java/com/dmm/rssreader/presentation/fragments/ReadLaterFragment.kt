package com.dmm.rssreader.presentation.fragments

import android.content.Intent
import android.util.Log
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
import com.dmm.rssreader.utils.NotificationsUI.Companion.snackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadLaterFragment : BaseFragment<ReadLaterFragmentBinding>(
	ReadLaterFragmentBinding::inflate
) {

	private lateinit var feedAdapter: FeedAdapter
	private lateinit var readLaterRV: RecyclerView
	private lateinit var totalFeedText: TextView

	override fun setupUI() {
		super.setupUI()

		readLaterRV = binding.listLayout.rvFeeds
		totalFeedText = binding.toolbarHome.totalFeeds
		setUpRecyclerView()

		viewModel.getFavouriteFeeds()
		collectFavouriteFeeds()
	}

	private fun collectFavouriteFeeds() {
		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.favouritesFeeds.collect {
				withContext(Dispatchers.Main) {
					totalFeedText.text = it.size.toString()
					binding.listLayout.search.visibility = if(it.isEmpty()) View.GONE else View.VISIBLE
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
			Log.e("handleSnackBar ---> ", "${feed.favourite}")
			viewModel.saveFavouriteFeed(feed) {
				viewModel.getFavouriteFeeds()
			}
		}
	}

	private fun setUpRecyclerView() = readLaterRV.apply {
		feedAdapter = FeedAdapter()
		adapter = feedAdapter
		layoutManager = LinearLayoutManager(requireContext())
		deleteItemSwipe()
		itemClickListener()
		readLaterItemClickListener()
		shareClickListener()
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

	private fun itemClickListener() = feedAdapter.setOnItemClickListener {
		val feedDescriptionDialog = FeedDescriptionDialog(it.copy())
		feedDescriptionDialog.show(parentFragmentManager, feedDescriptionDialog.tag)
	}

	private fun readLaterItemClickListener() = feedAdapter.setReadLaterOnItemClickListener {
		handleSnackBar(it)
	}

	private fun shareClickListener() = feedAdapter.setShareClickListener { list ->
		list[0].let {
			viewModel.logShare(list[1], list[2])
			val sendIntent: Intent = Intent().apply {
				action = Intent.ACTION_SEND
				putExtra(Intent.EXTRA_TEXT, it)
				type = "text/plain"
			}

			val shareIntent = Intent.createChooser(sendIntent, null)
			startActivity(shareIntent)
		}
	}
}