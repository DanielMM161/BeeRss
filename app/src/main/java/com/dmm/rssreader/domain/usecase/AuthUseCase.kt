package com.dmm.rssreader.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.domain.repositories.RepositoryAuth
import com.dmm.rssreader.utils.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthUseCase @Inject constructor(
	private val repositoryAuth: RepositoryAuth
) {

	suspend fun signInWithGoogle(authCredential: AuthCredential): Resource<UserProfile> {
		return repositoryAuth.signInWithGoogle(authCredential)
	}

	suspend fun createUserDocument(user: UserProfile): Resource<UserProfile> {
		return repositoryAuth.createUserDocument(user)
	}

	suspend fun getUserDocument(documentPath: String): Resource<UserProfile> {
		return repositoryAuth.getUserDocument(documentPath)
	}

	fun checkUserIsAuthenticated(): FirebaseUser? {
		return repositoryAuth.checkUserIsAuthenticated()
	}

	suspend fun signInEmailPassword(email: String, password: String): Resource<Boolean> {
		return repositoryAuth.signInEmailPassword(email, password)
	}

	suspend fun signUp(fullName: String, email: String, password: String): Resource<UserProfile> {
		return repositoryAuth.signUp(fullName, email, password)
	}

	fun signOut() {
		repositoryAuth.signOut()
	}

	fun resetPassword(email: String): MutableLiveData<Resource<Nothing>> {
		return repositoryAuth.resetPassword(email)
	}

	suspend fun sendEmailVerification(): Resource<Nothing> {
		return repositoryAuth.sendEmailVerification()
	}
}