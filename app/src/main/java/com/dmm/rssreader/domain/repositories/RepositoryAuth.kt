package com.dmm.rssreader.domain.repositories

import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.utils.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface RepositoryAuth {
	fun signInEmailPassword(email: String, password: String): MutableLiveData<Resource<Boolean>>
	fun createUserEmailPassword(email: String, password: String): MutableLiveData<Resource<UserProfile>>
	suspend fun signInWithGoogle(credential: AuthCredential): Resource<UserProfile>
	suspend fun createUserDocument(user: UserProfile): Resource<UserProfile>
	suspend fun getUserDocument(documentPath: String):  Resource<UserProfile>
	fun checkUserIsAuthenticated(): FirebaseUser?
	fun signOut()
	fun resetPassword(email: String): MutableLiveData<Resource<Nothing>>
	fun sendEmailVerification(): MutableLiveData<Resource<Nothing>>
}