package com.dmm.rssreader.domain.repositories

import com.dmm.rssreader.utils.Resource

interface RepositoryUser {
	suspend fun <T> updateUser(documentPath: String, data: T, property: String): Resource<Boolean>
}