package com.dmm.rssreader.presentation.fragments

import android.widget.ListView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmm.rssreader.databinding.SourcesFragmentBinding
import com.dmm.rssreader.domain.model.Source
import com.dmm.rssreader.presentation.adapters.SourcesFragmentAdapter
import com.dmm.rssreader.utils.NotificationsUI
import com.dmm.rssreader.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SourcesFragment : BaseFragment<SourcesFragmentBinding>(
	SourcesFragmentBinding::inflate
) {

	private lateinit var listViewSource: ListView
	private lateinit var listViewAdapter: SourcesFragmentAdapter

	override fun onViewCreated() {
		super.onViewCreated()
		setUpListView()
	}

	private fun setUpListView() {
		binding.listSources.apply {
			listViewAdapter = SourcesFragmentAdapter(viewModel.sources, viewModel.userProfile.sources) {source ->
				updateFollowSource(source)
			}
			adapter = listViewAdapter
		}
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


}