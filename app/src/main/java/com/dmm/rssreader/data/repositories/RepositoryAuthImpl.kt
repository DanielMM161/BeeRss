package com.dmm.rssreader.data.repositories

import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.R
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.domain.repositories.RepositoryAuth
import com.dmm.rssreader.utils.Constants
import com.dmm.rssreader.utils.Constants.SOURCE_ANDROID_BLOGS
import com.dmm.rssreader.utils.Constants.SOURCE_ANDROID_MEDIUM
import com.dmm.rssreader.utils.Constants.SOURCE_DANLEW_BLOG
import com.dmm.rssreader.utils.Constants.SOURCE_DEVELOPER_CO
import com.dmm.rssreader.utils.Constants.SOURCE_KOTLIN_WEEKLY
import com.dmm.rssreader.utils.Constants.USERS_COLLECTION
import com.dmm.rssreader.utils.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RepositoryAuthImpl @Inject constructor(
	private val firebaseAuth: FirebaseAuth,
	private val db: FirebaseFirestore,
) : RepositoryAuth{

	override fun signInEmailPassword(email: String, password: String): MutableLiveData<Resource<Boolean>> {
		val emailUser = MutableLiveData<Resource<Boolean>>(Resource.Loading())
		if(email.isNotEmpty() && password.isNotEmpty()) {
			firebaseAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener { task ->
					val currentUser = firebaseAuth.currentUser
					if(currentUser != null) {
						currentUser.let {
							val emailVerified = it.isEmailVerified
							if(emailVerified) {
								emailUser.value = checkSignInEmailPassword(task)
							} else if(!emailVerified) {
								emailUser.value = Resource.ErrorCaught(resId = R.string.verificate_email)
							} else {
								emailUser.value = Resource.ErrorCaught(resId = R.string.error_has_ocurred)
							}
						}
					} else {
						emailUser.value = checkSignInEmailPassword(task)
					}
				}
		} else {
			emailUser.value = Resource.ErrorCaught(resId = R.string.email_password_not_emptu)
		}
		return emailUser
	}

	private fun checkSignInEmailPassword(task: Task<AuthResult>): Resource<Boolean> {
		if(task.isSuccessful) {
			return Resource.Success(true)
		} else {
			return Resource.Error(task.exception?.message.toString())
		}
	}

	override fun createUserEmailPassword(email: String, password: String): MutableLiveData<Resource<UserProfile>> {
		val result = MutableLiveData<Resource<UserProfile>>(Resource.Loading())
		firebaseAuth.createUserWithEmailAndPassword(email, password)
			.addOnCompleteListener {
				if(it.isSuccessful) {
					val firebaseUser = firebaseAuth.currentUser
					val user = newUser("", email, firebaseUser?.uid!!, isNewUser = true)
					result.value = Resource.Success(user)
				} else {
					result.value = Resource.Error(it.exception?.message.toString())
				}
			}
		return result
	}

	override suspend fun signInWithGoogle(credential: AuthCredential): Resource<UserProfile> {
		return suspendCoroutine { continuation ->
			firebaseAuth.signInWithCredential(credential)
				.addOnCompleteListener { task ->
					val firebaseUser = firebaseAuth.currentUser
					val userProfile = firebaseUser?.run {
						newUser(firebaseUser.displayName!!, firebaseUser.email!!, firebaseUser.uid, isNewUser = true)
					}
					val result = if (task.isSuccessful && firebaseUser != null) {
						Resource.Success(userProfile)
					} else {
						Resource.Error(task.exception?.message ?: "Error desconocido")
					}
					continuation.resume(result)
				}
		}
	}

	override suspend fun createUserDocument(user: UserProfile): Resource<UserProfile?> = suspendCoroutine { continuation ->
		val docRef = db.collection(USERS_COLLECTION).document(user.email)
		docRef.set(user)
			.addOnCompleteListener {
				if(!it.isSuccessful) {
					continuation.resume(Resource.Error(it.exception?.message.toString()))
				} else {
					continuation.resume(Resource.Success(user))
				}
			}
	}

	override fun sendEmailVerification(): MutableLiveData<Resource<Nothing>> {
		val result =  MutableLiveData<Resource<Nothing>>(Resource.Loading())
		val firebaseUser = firebaseAuth.currentUser
		firebaseUser?.sendEmailVerification()?.addOnCompleteListener {
			if(it.isSuccessful) {
				result.value = Resource.Success()
			} else {
				result.value = Resource.Error(it.exception?.message.toString())
			}
		}
		return result
	}

	override suspend fun getUserDocument(email: String): Resource<UserProfile> {
		val docRef = db.collection(USERS_COLLECTION).document(email)
		return suspendCancellableCoroutine { continuation ->
			docRef.get()
				.addOnCompleteListener { task ->
					val document = task.result
					val resource = if (document.exists() ) {
						val userProfile = document.toObject(UserProfile::class.java)
						Resource.Success(userProfile)
					} else {
						Resource.Error("User not found")
					}
					continuation.resume(resource)
				}
				.addOnFailureListener { exception ->
					continuation.resume(Resource.Error(exception.message ?: "Unexpected Error"))
				}
		}
	}

	override fun checkIfUserIsAuthenticatedInFireBase(): MutableLiveData<UserProfile> {
		val user = MutableLiveData(UserProfile())
		val fireAuth = firebaseAuth.currentUser
		if(fireAuth != null) {
			val userAuthenticated = UserProfile(
				isAuthenticated = true,
				userUid = fireAuth.uid,
				email = fireAuth.email!!,
				fullName = fireAuth.displayName!!
			)
			user.value = userAuthenticated
		}
		return user
	}

	override fun signOut() {
		firebaseAuth.signOut()
	}

	override fun resetPassword(email: String): MutableLiveData<Resource<Nothing>> {
		val result = MutableLiveData<Resource<Nothing>>(Resource.Loading())
		firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
			if(!it.isSuccessful) {
				result.value = Resource.Error(message = it.exception?.message.toString())
			} else {
				result.value = Resource.Success()
			}
		}
		return result
	}

	private fun newUser(fullName: String, email: String, userUid: String, isNewUser: Boolean): UserProfile {
		return UserProfile(
			fullName = fullName,
			email = email,
			userUid = userUid,
			isNew = isNewUser,
			theme = Constants.THEME_DAY,
			feeds = mutableListOf(
				SOURCE_ANDROID_MEDIUM,
				SOURCE_ANDROID_BLOGS,
				SOURCE_KOTLIN_WEEKLY,
				SOURCE_DANLEW_BLOG,
				SOURCE_DEVELOPER_CO
			)
		)
	}
}