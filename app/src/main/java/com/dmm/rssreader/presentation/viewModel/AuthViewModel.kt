package com.dmm.rssreader.presentation.viewModel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.domain.usecase.AuthUseCase
import com.dmm.rssreader.domain.usecase.ValidateUseCase
import com.dmm.rssreader.utils.Resource
import com.dmm.rssreader.utils.ValidationResult
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
	app: Application,
	private val authUseCase: AuthUseCase,
	private val validateUseCase: ValidateUseCase,
	private val firebaseAnalytics: FirebaseAnalytics
) : AndroidViewModel(app) {

	suspend fun signInWithGoogle(authCredential: AuthCredential): Resource<UserProfile> {
		return authUseCase.signInWithGoogle(authCredential)
	}

	suspend fun createUserDocument(user: UserProfile): Resource<UserProfile> {
		return authUseCase.createUserDocument(user)
	}

	suspend fun getUserDocument(documentPath: String): Resource<UserProfile>{
		return authUseCase.getUserDocument(documentPath)
	}

	suspend fun signUp(fullName: String, email: String, password: String): Resource<UserProfile> {
		return authUseCase.signUp(fullName, email, password)
	}

	fun checkUserIsAuthenticated(): FirebaseUser? {
		return authUseCase.checkUserIsAuthenticated()
	}

	suspend fun signInEmailPassword(email: String, password: String): Resource<Boolean>  {
		return authUseCase.signInEmailPassword(email, password)
	}

	fun validateEmail(email: String): ValidationResult {
		return validateUseCase.validateEmail(email)
	}

	fun validateFullName(fullName: String): ValidationResult {
		return validateUseCase.validateFullName(fullName)
	}

	fun validatePassword(password: String): ValidationResult {
		return validateUseCase.validatePassword(password)
	}

	fun validateRepeatPassword(password: String, repeatPassword: String): ValidationResult {
		return validateUseCase.validateRepeatedPassword(password, repeatPassword)
	}

	fun resetPassword(email: String): MutableLiveData<Resource<Nothing>> {
		return authUseCase.resetPassword(email)
	}

	suspend fun sendEmailVerification(): Resource<Nothing> {
		return authUseCase.sendEmailVerification()
	}

	fun logEvent(parameter: String) {
		val params = Bundle()
		params.putString(FirebaseAnalytics.Param.METHOD, parameter)
		firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
	}

}