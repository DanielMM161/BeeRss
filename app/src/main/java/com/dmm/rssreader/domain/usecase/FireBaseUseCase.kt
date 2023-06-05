package com.dmm.rssreader.domain.usecase

import com.dmm.rssreader.data.repositories.RepositoryFireBaseImpl
import com.dmm.rssreader.utils.Resource
import javax.inject.Inject

class FireBaseUseCase @Inject constructor(
	private val repository: RepositoryFireBaseImpl
) {

	suspend fun <T> updateUser(documentPath: String, data: T, property: String): Resource<Boolean> {
		return repository.updateUser(documentPath, data, property)
	}
}