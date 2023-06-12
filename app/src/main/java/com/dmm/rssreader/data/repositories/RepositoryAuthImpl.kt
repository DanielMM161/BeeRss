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
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RepositoryAuthImpl @Inject constructor(
	private val firebaseAuth: FirebaseAuth,
	private val db: FirebaseFirestore,
) : RepositoryAuth{

	override suspend fun signInEmailPassword(email: String, password: String): Resource<Boolean> {
		return suspendCoroutine { continuation ->
			firebaseAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						val result = if (task.result.user?.isEmailVerified!!) {
							Resource.Success(true)
						} else {
							Resource.ErrorCaught(resId = R.string.verificate_email)
						}
						continuation.resume(result)
					}
				}
				.addOnFailureListener {
					continuation.resume(Resource.Error(it.message ?: "Unkown Error in Sign In"))
				}
		}
	}

	override suspend fun signUp(fullName: String, email: String, password: String): Resource<UserProfile> {
		return suspendCoroutine{ continuation ->
			firebaseAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener {
					val currentUser = it.result.user
					val result = if (it.isSuccessful && currentUser != null) {
						Resource.Success(newUser(fullName, email, currentUser?.uid!!))
					} else {
						Resource.Error(it.exception?.message.toString())
					}
					continuation.resume(result)
				}
				.addOnFailureListener {
					continuation.resume(Resource.Error(it.message ?: "Unknown Error in SignUp"))
				}
		}
	}

	override suspend fun signInWithGoogle(credential: AuthCredential): Resource<UserProfile> {
		return suspendCoroutine { continuation ->
			firebaseAuth.signInWithCredential(credential)
				.addOnCompleteListener { task ->
					val firebaseUser = firebaseAuth.currentUser
					val result = if (task.isSuccessful && firebaseUser != null) {
						Resource.Success(newUser(firebaseUser.displayName!!, firebaseUser.email!!, firebaseUser.uid))
					} else {
						Resource.Error(task.exception?.message ?: "Error desconocido")
					}
					continuation.resume(result)
				}
		}
	}

	override suspend fun createUserDocument(user: UserProfile): Resource<UserProfile> {
		return suspendCoroutine { continuation ->
			val document = db.collection(USERS_COLLECTION).document(user.email)
			document.set(user)
				.addOnCompleteListener {
					val result = if (it.isSuccessful) {
						Resource.Success(user)
					} else {
						Resource.Error(it.exception?.message ?: "Unkown Error Create User")
					}
					continuation.resume(result)
				}
				.addOnFailureListener {
					continuation.resume(Resource.Error(it.message ?: "Unkown Error Create User"))
				}
		}
	}

	override suspend fun sendEmailVerification(): Resource<Nothing> {
		return suspendCoroutine { continuation ->
			val firebaseUser = firebaseAuth.currentUser
			firebaseUser?.sendEmailVerification()
				?.addOnCompleteListener {
					val result = if(it.isSuccessful) {
						Resource.Success(null)
					} else {
						Resource.Error(it.exception?.message.toString())
					}
					continuation.resume(result)
				}
				?.addOnFailureListener {
					continuation.resume(Resource.Error(it.message ?: "Unkown error in Send Email Verification"))
				}
		}
	}

	override suspend fun getUserDocument(documentPath: String): Resource<UserProfile> {
		return suspendCoroutine { continuation ->
			val document = db.collection(USERS_COLLECTION).document(documentPath)
			document.get()
				.addOnCompleteListener {
					val docu = it.result
					val result = if (docu.exists()) {
						val userProfile = docu.toObject(UserProfile::class.java)
						Resource.Success(userProfile)
					} else {
						Resource.Error("User not found")
					}
					continuation.resume(result)
				}
				.addOnFailureListener {
					continuation.resume(Resource.Error(it.message ?: "Unkown Error : User not found"))
				}
		}
	}

	override fun checkUserIsAuthenticated(): FirebaseUser? {
		return firebaseAuth.currentUser
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

	private fun newUser(fullName: String, email: String, userUid: String): UserProfile {
		return UserProfile(
			fullName = fullName,
			email = email,
			userUid = userUid,
			theme = Constants.THEME_DAY,
			sources = mutableListOf()
		)
	}
}