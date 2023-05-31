package com.dmm.rssreader.domain.repositories

import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.utils.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface RepositoryAuth {
	suspend fun signInEmailPassword(email: String, password: String): Resource<Boolean>
	suspend fun signUp(fullName: String, email: String, password: String): Resource<UserProfile>
	suspend fun signInWithGoogle(credential: AuthCredential): Resource<UserProfile>
	suspend fun createUserDocument(user: UserProfile): Resource<UserProfile>
	suspend fun getUserDocument(documentPath: String):  Resource<UserProfile>
	suspend fun sendEmailVerification(): Resource<Nothing>
	fun checkUserIsAuthenticated(): FirebaseUser?
	fun signOut()
	fun resetPassword(email: String): MutableLiveData<Resource<Nothing>>

}