package com.dmm.rssreader.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.dmm.rssreader.data.repositories.RepositoryFireBaseImpl
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.utils.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

class FireBaseUseCase @Inject constructor(
	private val repository: RepositoryFireBaseImpl
) {

	fun saveUser(userProfile: UserProfile): MutableLiveData<Resource<Nothing>> {
		return repository.saveUser(userProfile)
	}
}