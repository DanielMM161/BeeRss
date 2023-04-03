package com.dmm.rssreader.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ActivitySplashScreenBinding
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.presentation.viewModel.AuthViewModel
import com.dmm.rssreader.utils.Constants
import com.dmm.rssreader.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

	private lateinit var binding: ActivitySplashScreenBinding
	private lateinit var authViewModel: AuthViewModel

	companion object {
		var SPLASH_SCREEN: Long = 2000;
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivitySplashScreenBinding.inflate(layoutInflater)
		authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
		setContentView(binding.root)
		binding.loadingFeedback.text = getString(R.string.checking_credentials)

		Handler(Looper.getMainLooper()).postDelayed({
			checkIfUserAuthenticated()
		}, SPLASH_SCREEN)
	}

	private fun checkIfUserAuthenticated() {
		authViewModel.checkIfUserIsAuthenticatedInFireBase()
		authViewModel.authUser.observe(this) { user ->
			if(!user.isAuthenticated) {
				goToLoginActivity()
			} else {
				getUserDocument(user.email)
			}
		}
	}

	private fun getUserDocument(documentPath: String) {
		binding.loadingFeedback.text = getString(R.string.upload_data)
		authViewModel.getUserDocument(documentPath)
		authViewModel.currentUser.observe(this) {
			when(it) {
				is Resource.Success -> {
					val user = it.data
					if(user != null) {
						goToMainActivity(user)
					}
				}
				is Resource.ErrorCaught -> {
					goToLoginActivity()
				}
				is Resource.Error -> {
					goToLoginActivity()
				}
				else -> {}
			}
		}
	}

	private fun goToMainActivity(user: UserProfile) {
		val intent = Intent(this, MainActivity::class.java)
		intent.putExtra(Constants.USER_KEY, user)
		startActivity(intent)
		finish()
	}

	private fun goToLoginActivity() {
		val intent = Intent(this, AuthActivity::class.java)
		startActivity(intent)
		finish()
	}
}