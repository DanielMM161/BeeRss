package com.dmm.rssreader.presentation.fragments

import android.content.Intent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ReadLaterFragmentBinding
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.presentation.adapters.FeedAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ReadLaterFragment : BaseFragment<ReadLaterFragmentBinding>(
	ReadLaterFragmentBinding::inflate
) {

	private lateinit var feedAdapter: FeedAdapter

	override fun setupUI() {
		super.setupUI()
		setUpRecyclerView()
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				launch {
					viewModel.getFavouriteFeeds()
				}
				launch {
					viewModel.favouritesFeeds.collect {
						binding.noReadLater.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
						binding.willBeHere.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
						feedAdapter.differ.submitList(it)
					}
				}
			}
		}
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
				snackBar(feed)
			}
		}

		ItemTouchHelper(itemTouchHelperCallback).apply {
			attachToRecyclerView(binding.rvReadLater)
		}
	}

	private fun snackBar(feed: FeedUI) {
		viewModel.saveFavouriteFeed(feed)
		Snackbar.make(binding.root, getString(R.string.delete_feed, feed.title), Snackbar.LENGTH_LONG).apply {
			setAction(getString(R.string.undo)) {
				viewModel.saveFavouriteFeed(feed)
			}
			show()
		}
	}

	private fun setUpRecyclerView() = binding.rvReadLater.apply {
		feedAdapter = FeedAdapter()
		adapter = feedAdapter
		layoutManager = LinearLayoutManager(requireContext())
		deleteItemSwipe()
		itemClickListener()
		readLaterItemClickListener()
		shareClickListener()
	}

	private fun itemClickListener() = feedAdapter.setOnItemClickListener {
		val feedDescriptionDialog = FeedDescriptionDialog(it.copy())
		feedDescriptionDialog.show(parentFragmentManager, feedDescriptionDialog.tag)
	}

	private fun readLaterItemClickListener() = feedAdapter.setReadLaterOnItemClickListener {
		snackBar(it)
	}

	private fun shareClickListener() = feedAdapter.setShareClickListener { list ->
		list[0]?.let {
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