package com.dmm.rssreader.presentation.fragments

import android.content.DialogInterface
import com.dmm.rssreader.data.local.ContentResources.contentResources
import com.dmm.rssreader.databinding.SourcesDialogFragmentBinding
import com.dmm.rssreader.presentation.adapters.SourcesAdapter
import com.dmm.rssreader.utils.Resource
import com.dmm.rssreader.utils.Utils.Companion.showToast

class SourcesDialogFragment : BaseBottomSheetDialogFragment<SourcesDialogFragmentBinding>(
	SourcesDialogFragmentBinding::inflate
) {

	private var onCancelClick: (() -> Unit)? = null

	override fun setupUI() {
		super.setupUI()
		binding.listSources.apply {
			adapter = SourcesAdapter(contentResources, viewModel.userProfile.feeds) { title, _ ->
				setFeed(title)
			}
		}
	}

  private fun setFeed(feed: String) {
    viewModel.setFeed(feed).observe(this) {
      when(it) {
        is Resource.Error -> {
					showToast(context, it.message)
        }
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