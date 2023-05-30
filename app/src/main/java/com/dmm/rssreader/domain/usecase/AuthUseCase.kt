package com.dmm.rssreader.domain.usecase

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.domain.repositories.RepositoryAuth
import com.dmm.rssreader.utils.Resource
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class AuthUseCase @Inject constructor(
	private val repositoryAuth: RepositoryAuth
) {

	suspend fun signInWithGoogle(authCredential: AuthCredential): Resource<UserProfile> {
		return repositoryAuth.signInWithGoogle(authCredential)
	}

	suspend fun createUserDocument(user: UserProfile): Resource<UserProfile?> {
		return repositoryAuth.createUserDocument(user)
	}

	suspend fun getUserDocument(documentPath: String): Resource<UserProfile> {
		return repositoryAuth.getUserDocument(documentPath)
	}

	fun checkIfUserIsAuthenticatedInFireBase(): MutableLiveData<UserProfile> {
		return repositoryAuth.checkIfUserIsAuthenticatedInFireBase()
	}

	fun signInEmailPassword(email: String, password: String): MutableLiveData<Resource<Boolean>> {
		return repositoryAuth.signInEmailPassword(email, password)
	}

	fun createUserEmailPassword(email: String, password: String): MutableLiveData<Resource<UserProfile>> {
		return repositoryAuth.createUserEmailPassword(email, password)
	}

	fun signOut() {
		repositoryAuth.signOut()
	}

	fun resetPassword(email: String): MutableLiveData<Resource<Nothing>> {
		return repositoryAuth.resetPassword(email)
	}

	fun sendEmailVerification(): MutableLiveData<Resource<Nothing>> {
		return repositoryAuth.sendEmailVerification()
	}
}