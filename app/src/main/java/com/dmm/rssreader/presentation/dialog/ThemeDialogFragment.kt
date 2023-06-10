package com.dmm.rssreader.presentation.dialog

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.forEach
import com.dmm.rssreader.databinding.ItemThemeOptionsBinding
import com.dmm.rssreader.utils.Constants.THEME_DAY
import com.dmm.rssreader.utils.Constants.THEME_NIGHT

class ThemeDialogFragment : CustomBottomDialog<ItemThemeOptionsBinding>(
	ItemThemeOptionsBinding::inflate
) {

	override fun onViewCreated() {
		super.onViewCreated()
		autoSelectedTheme()
		selectedTheme()
	}

	private fun selectedTheme() {
		binding.layoutDay.setOnClickListener {
			selectedView(binding.layoutDay, true)
			selectedView(binding.layoutNight, false)
			setTheme(THEME_DAY)
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
		}

		binding.layoutNight.setOnClickListener {
			selectedView(binding.layoutNight, true)
			selectedView(binding.layoutDay, false)
			setTheme(THEME_NIGHT)
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
		}
	}

	private fun autoSelectedTheme() {
		when (viewModel.userProfile.theme) {
			THEME_DAY -> {
				selectedView(binding.layoutParent, true)
			}
			THEME_NIGHT -> {
				selectedView(binding.layoutParent, true)
			}
		}
	}

	private fun setTheme(theme: String) {
	/*	viewModel.setTheme(theme).observe(this) {
			when (it) {
				is Resource.Success -> {
					if (it.data!!) {
						// GIVE FEEDBACK TO USER
					}
				}
			}
		}*/
	}

	private fun selectedView(view: View, selected: Boolean) {
		(view as ViewGroup).forEach {
			it.isSelected = selected
		}
	}
}