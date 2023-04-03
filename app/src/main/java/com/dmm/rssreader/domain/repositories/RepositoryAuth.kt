package com.dmm.rssreader.domain.repositories

import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.utils.Resource
import com.google.firebase.auth.AuthCredential

interface RepositoryAuth {
	fun signInEmailPassword(email: String, password: String): MutableLiveData<Resource<Boolean>>
	fun createUserEmailPassword(email: String, password: String): MutableLiveData<Resource<UserProfile>>
	fun signInWithGoogle(credential: AuthCredential): MutableLiveData<UserProfile>
	fun createUserDocument(user: UserProfile):  MutableLiveData<Resource<UserProfile>>
	fun getUserDocument(documentPath: String):MutableLiveData<Resource<UserProfile>>
	fun checkIfUserIsAuthenticatedInFireBase(): MutableLiveData<UserProfile>
	fun signOut()
	fun resetPassword(email: String): MutableLiveData<Resource<Nothing>>
	fun sendEmailVerification(): MutableLiveData<Resource<Nothing>>
}