package com.dmm.rssreader.data.repositories

import com.dmm.rssreader.domain.repositories.RepositoryUser
import com.dmm.rssreader.utils.Constants
import com.dmm.rssreader.utils.Resource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RepositoryUserImpl @Inject constructor(
  private val db: FirebaseFirestore,
) : RepositoryUser {

  override suspend fun <T> updateUser(documentPath: String, data: T, property: String): Resource<Boolean> {
    return suspendCoroutine { continuation ->
      getDBCollection(documentPath)
        .update(mapOf( property to data))
        .addOnCompleteListener { task ->
          val result = if (task.isSuccessful) {
            Resource.Success(true)
          } else {
            Resource.Error(task.exception?.message.toString())
          }
          continuation.resume(result)
        }
    }
  }


  private fun getDBCollection(documentPath: String?): DocumentReference {
    return db.collection(Constants.USERS_COLLECTION).document(documentPath!!)
  }
}