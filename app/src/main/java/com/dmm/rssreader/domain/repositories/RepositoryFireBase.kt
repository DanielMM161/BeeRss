package com.dmm.rssreader.domain.repositories

import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.utils.Resource
import com.google.firebase.firestore.DocumentReference

interface RepositoryFireBase {
	fun saveUser(userProfile: UserProfile): MutableLiveData<Resource<Nothing>>
	fun getDBCollection(documentPath: String?): DocumentReference
}