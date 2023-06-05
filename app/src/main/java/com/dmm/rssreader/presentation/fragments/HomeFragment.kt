package com.dmm.rssreader.presentation.fragments

import android.content.Intent
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.HomeFragmentBinding
import com.dmm.rssreader.domain.extension.gone
import com.dmm.rssreader.domain.extension.show
import com.dmm.rssreader.presentation.activities.MainActivity
import com.dmm.rssreader.presentation.adapters.FeedAdapter
import com.dmm.rssreader.utils.Utils.Companion.isNightMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : BaseFragment<HomeFragmentBinding>(
	HomeFragmentBinding::inflate
) {

	private lateinit var feedAdapter: FeedAdapter

	override fun setupUI() {
		super.setupUI()

		lifecycleScope.launch(Dispatchers.IO) {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.fetchFeedsDeveloper()
			}
		}

		setUpRecyclerView()
		onRefreshListener()
		searchFeed()
		setColorSwipeRefresh()
		collectFeedsDeveloper()
	}

	private fun searchFeed() {
		binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				query?.let {
					if(it.isNotEmpty()) {
						viewModel.searchText = query.trim()
						searchFeeds()
					}
				}
				return false
			}

			override fun onQueryTextChange(text: String?): Boolean {
				if(text != null) {
					viewModel.searchText = text.trim()
					searchFeeds()
				}
				return false
			}
		})
	}

	private fun setColorSwipeRefresh() {
		when(isNightMode(resources)) {
			true -> {
				binding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.primary)
				binding.swipeRefresh.setColorSchemeResources(R.color.black)
			}
			false -> {
				binding.swipeRefresh.setColorSchemeResources(R.color.primary)
			}
		}
	}

	private fun setUpRecyclerView() = binding.rvFeeds.apply {
		feedAdapter = FeedAdapter()
		adapter = feedAdapter
		layoutManager = LinearLayoutManager(requireContext())
		itemClickListener()
		readLaterItemClickListener()
		shareClickListener()
	}

	/**
	 * When user drag down the swipe,
	 * drop the local database and  fetch the data again
	 */
	private fun onRefreshListener() {
		binding.swipeRefresh.setOnRefreshListener {
			viewModel.deleteTable()
			viewModel.fetchFeedsDeveloper()
		}
	}

	private fun collectFeedsDeveloper() {
		binding.swipeRefresh.isRefreshing = true
		lifecycleScope.launch(Dispatchers.IO) {
			withContext(Dispatchers.Main) {
				viewModel.developerFeeds.collect {
					val feeds = it.data ?: listOf()
					if(feeds.isEmpty()) {
						showNoItemText()
					} else {
						binding.noItemText.gone()
						binding.noItemBtn.gone()
					}
					binding.totalArticles = feeds.size
					setMaterialToolbarFromActivity(feeds.size.toString())
					if(viewModel.searchText.isNotEmpty()) {
						searchFeeds()
					} else {
						feedAdapter.differ.submitList(feeds)
					}
					binding.swipeRefresh.isRefreshing = false
					feedAdapter.differ.submitList(feeds)
				}
			}
		}
	}

	private fun itemClickListener() = feedAdapter.setOnItemClickListener {
		viewModel.logSelectItem(it.feedSource)
		val feedDescriptionDialog = FeedDescriptionDialog(it.copy())
		feedDescriptionDialog.show(parentFragmentManager, feedDescriptionDialog.tag)
	}

	private fun readLaterItemClickListener() = feedAdapter.setReadLaterOnItemClickListener {
		viewModel.saveFavouriteFeed(it)
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

	private fun setMaterialToolbarFromActivity(feedsSize: String?) {
		(activity as MainActivity?)?.setTitleMateriaToolbar(R.string.title_home_fragment, feedsSize ?: "")
	}

	private fun searchFeeds() {
		val text = viewModel.searchText
		if(text.isNotEmpty()) {
			val list = viewModel.findFeeds(text)
			if(list != null) {
				setMaterialToolbarFromActivity(list.size.toString())
				feedAdapter.differ.submitList(list)
			}
		} else {
			val list = viewModel.developerFeeds.value.data
			setMaterialToolbarFromActivity(list?.size.toString())
			feedAdapter.differ.submitList(list)
		}
	}

	private fun showNoItemText() {
		binding.noItemText.show()
		binding.noItemBtn.apply {
			show()
			setOnClickListener {
				val sourcesDialogFragment = SourcesDialogFragment()
				sourcesDialogFragment.setOnCancelClick {
					viewModel.fetchFeedsDeveloper()
				}
				sourcesDialogFragment.show(parentFragmentManager, sourcesDialogFragment.tag)
			}
		}
	}
}