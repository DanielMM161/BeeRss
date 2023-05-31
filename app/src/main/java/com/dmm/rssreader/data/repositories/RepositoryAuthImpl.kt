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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.internal.wait
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
		return suspendCoroutine { continuation ->
			var document = db.collection(USERS_COLLECTION).document(email)
			document.get()
				.addOnCompleteListener {
					var docu = it.result
					var result = if (docu.exists()) {
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