package com.dmm.rssreader.presentation.viewModel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.domain.usecase.AuthUseCase
import com.dmm.rssreader.domain.usecase.ValidateUseCase
import com.dmm.rssreader.utils.Resource
import com.dmm.rssreader.utils.ValidationResult
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
	app: Application,
	private val authUseCase: AuthUseCase,
	private val validateUseCase: ValidateUseCase,
	private val firebaseAnalytics: FirebaseAnalytics
) : AndroidViewModel(app) {

	var userShare: UserProfile? = null

	suspend fun signInWithGoogle(authCredential: AuthCredential): Resource<UserProfile> {
		return authUseCase.signInWithGoogle(authCredential)
	}

	suspend fun createUserDocument(user: UserProfile): Resource<UserProfile> {
		return authUseCase.createUserDocument(user)
	}

	suspend fun getUserDocument(documentPath: String): Resource<UserProfile>{
		return authUseCase.getUserDocument(documentPath)
	}

	fun createUserEmailPassword(email: String, password: String): MutableLiveData<Resource<UserProfile>> {
		return authUseCase.createUserEmailPassword(email, password)
	}

	fun checkUserIsAuthenticated(): FirebaseUser? {
		return authUseCase.checkUserIsAuthenticated()
	}

	fun signInEmailPassword(email: String, password: String): MutableLiveData<Resource<Boolean>>  {
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

	fun sendEmailVerification(): MutableLiveData<Resource<Nothing>> {
		return authUseCase.sendEmailVerification()
	}

	fun logEvent(parameter: String) {
		val params = Bundle()
		params.putString(FirebaseAnalytics.Param.METHOD, parameter)
		firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
	}

}