package com.dmm.rssreader.data.repositories

import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.domain.repositories.RepositoryFireBase
import com.dmm.rssreader.utils.Constants
import com.dmm.rssreader.utils.Resource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RepositoryFireBaseImpl @Inject constructor(
  private val db: FirebaseFirestore,
) : RepositoryFireBase {

  override fun saveUser(userProfile: UserProfile): MutableLiveData<Resource<Nothing>> {
    val result = MutableLiveData<Resource<Nothing>>(Resource.Loading())
    getDBCollection(userProfile.email)
      .set(userProfile)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          result.value = Resource.Success()
        } else {
          result.value = Resource.Error(task.exception?.message.toString())
        }
      }
    return result
  }

  override fun getDBCollection(documentPath: String?): DocumentReference {
    return db.collection(Constants.USERS_COLLECTION).document(documentPath!!)
  }
}