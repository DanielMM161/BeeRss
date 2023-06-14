package com.dmm.rssreader.presentation.fragments

import androidx.lifecycle.lifecycleScope
import com.dmm.rssreader.databinding.SourcesFragmentBinding
import com.dmm.rssreader.domain.model.Source
import com.dmm.rssreader.presentation.adapters.SourcesFragmentAdapter
import com.dmm.rssreader.presentation.dialog.FeedListDialog
import com.dmm.rssreader.utils.NotificationsUI
import com.dmm.rssreader.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SourcesFragment : BaseFragment<SourcesFragmentBinding>(
	SourcesFragmentBinding::inflate
), SourcesFragmentAdapter.Callbacks {

	override fun onViewCreated() {
		super.onViewCreated()
		setUpListView()
	}

	private fun setUpListView() {
		binding.listSources.adapter = SourcesFragmentAdapter(viewModel.sources, viewModel.userProfile.sources, this)
	}

	private fun updateFollowSource(item: Source) {
		viewModel.setUserSources(item)
		lifecycleScope.launch(Dispatchers.IO) {
			val feeds = viewModel.userProfile.sources
			val result = viewModel.updateUser(feeds, "sources")
			when (result) {
				is Resource.Error -> {
					NotificationsUI.showToast(context, result.message)
				}
				else -> {}
			}
		}
	}

	override fun onItemClick(item: Source) {
		FeedListDialog(item).show(childFragmentManager, "FeedListDialog")
	}

	override fun onFollowClick(item: Source) {
		updateFollowSource(item)
	}


}