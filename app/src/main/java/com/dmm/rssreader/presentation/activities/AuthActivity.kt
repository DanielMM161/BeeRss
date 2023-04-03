package com.dmm.rssreader.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

	private lateinit var binding: ActivityAuthBinding
	private lateinit var navController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityAuthBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val navHostFragment = supportFragmentManager
			.findFragmentById(R.id.fragment_auth_container) as NavHostFragment
		navController = navHostFragment.navController
	}

	override fun onBackPressed() {
			when(navController.currentDestination?.id) {
				R.id.forgetPasswordFragment -> {
					navController.navigate(R.id.action_forgetPasswordFragment_to_loginFragment)
				}
				R.id.registerFragment -> {
					navController.navigate(R.id.action_registerFragment_to_loginFragment)
				}
			}
	}

}