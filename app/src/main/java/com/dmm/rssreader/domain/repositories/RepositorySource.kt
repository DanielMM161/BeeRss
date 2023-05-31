package com.dmm.rssreader.domain.repositories

import com.dmm.rssreader.domain.model.Source
import kotlinx.coroutines.flow.Flow

interface RepositorySource {
	suspend fun fetchSources(): Flow<List<Source>>
}