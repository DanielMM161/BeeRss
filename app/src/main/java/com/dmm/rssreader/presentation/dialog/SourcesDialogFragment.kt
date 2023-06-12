package com.dmm.rssreader.presentation.dialog

import android.content.DialogInterface
import androidx.lifecycle.lifecycleScope
import com.dmm.rssreader.databinding.SourcesDialogFragmentBinding
import com.dmm.rssreader.domain.model.Source
import com.dmm.rssreader.presentation.adapters.SourcesDialogAdapter
import com.dmm.rssreader.utils.NotificationsUI.Companion.showToast
import com.dmm.rssreader.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SourcesDialogFragment : CustomBottomDialog<SourcesDialogFragmentBinding>(
	SourcesDialogFragmentBinding::inflate
) {

	private var onCancelClick: (() -> Unit)? = null

	override fun onViewCreated() {
		super.onViewCreated()
		binding.listSources.apply {
			adapter = SourcesDialogAdapter(viewModel.sources, viewModel.userProfile.sources) { source ->
				setFeed(source)
			}
		}
	}

	private fun setFeed(source: Source) {
		viewModel.setUserSources(source)

		lifecycleScope.launch(Dispatchers.IO) {
			val feeds = viewModel.userProfile.sources
			val result = viewModel.updateUser(feeds, "sources")
			when (result) {
				is Resource.Error -> {
					showToast(context, result.message)
				}
				else -> {}
			}
		}

	}

	fun setOnCancelClick(listener: () -> Unit) {
		onCancelClick = listener
	}

	override fun onCancel(dialog: DialogInterface) {
		super.onCancel(dialog)
		onCancelClick?.let { it() }
	}
}