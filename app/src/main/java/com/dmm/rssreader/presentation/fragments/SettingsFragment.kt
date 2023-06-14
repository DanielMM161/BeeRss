package com.dmm.rssreader.presentation.fragments

import android.content.Intent
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.SettingsFragmentBinding
import com.dmm.rssreader.presentation.activities.AuthActivity
import com.dmm.rssreader.presentation.activities.MainActivity
import com.dmm.rssreader.presentation.dialog.ThemeDialogFragment
import com.dmm.rssreader.utils.Utils.Companion.alertDialog

class SettingsFragment : BaseFragment<SettingsFragmentBinding>(
  SettingsFragmentBinding::inflate
) {

  override fun onViewCreated() {
    super.onViewCreated()

    binding.name.text = viewModel.userProfile.fullName
    binding.email.text = viewModel.userProfile.email
    binding.feedsNumber.text = viewModel.developerFeeds.value.data?.size.toString()
    binding.favouritesNumber.text = viewModel.userProfile.favouritesFeeds.size.toString()

    logout()
    themesOption()
  }

  private fun themesOption() {
    binding.userThemeLayout.setOnClickListener {
      val themeDialogFragment = ThemeDialogFragment()
      themeDialogFragment.show(parentFragmentManager, themeDialogFragment.tag)
    }
  }

  private fun logout() {
    binding.logoutBtn.setOnClickListener {
      alertDialog(
        context = context,
        message = getString(R.string.message_logout),
        title = getString(R.string.title_logout),
        textPositiveButton = getString(R.string.accept),
        textNegativeButton = getString(R.string.cancel)
      ) {
        viewModel.signOut()
        val intent = Intent(context, AuthActivity::class.java)
        startActivity(intent)
        (activity as MainActivity?)?.finish()
      }
    }
  }
}