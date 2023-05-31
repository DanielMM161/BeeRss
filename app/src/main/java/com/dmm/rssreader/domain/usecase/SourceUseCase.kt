package com.dmm.rssreader.domain.usecase

import com.dmm.rssreader.domain.model.Source
import com.dmm.rssreader.domain.repositories.RepositorySource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SourceUseCase @Inject constructor(
	private val repo: RepositorySource
) {

	suspend fun fetchSources(): Flow<List<Source>> {
		return repo.fetchSources()
	}
}