package com.dmm.rssreader.presentation.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ActivityMainBinding
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.presentation.viewModel.MainViewModel
import com.dmm.rssreader.utils.Constants.USER_KEY
import com.dmm.rssreader.utils.Utils.Companion.isNightMode
import dagger.hilt.android.AndroidEntryPoint

@Suppress("UNREACHABLE_CODE")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private lateinit var viewModel: MainViewModel
	private lateinit var navController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
		setContentView(binding.root)

		val navHostFragment = supportFragmentManager
			.findFragmentById(R.id.fragment_container) as NavHostFragment
		navController = navHostFragment.navController

		binding.bottomNavigation.setupWithNavController(navController)

		if(!viewModel.userProfileInitialized()) {
			viewModel.userProfile = getUserFromActivity()
		}

		setShadowColor()
	}

	private fun getUserFromActivity(): UserProfile {
		var userProfile = UserProfile()
		val extras = intent.extras
		if(extras != null) {
			userProfile = extras.getParcelable(USER_KEY)!!
		}
		return userProfile
	}

	private fun setShadowColor() {
		when(isNightMode(resources)) {
			true -> {
				binding.bottomShadow.background = getDrawable(R.drawable.shadow_bottom_navigation_dark)
			}
			else -> {}
		}
	}

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp() || super.onSupportNavigateUp()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> {
				onBackPressed()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}
}