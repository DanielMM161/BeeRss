package com.dmm.rssreader.di

import com.dmm.rssreader.data.repositories.*
import com.dmm.rssreader.domain.repositories.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {

	@Binds
	abstract fun bindRepositoryAuth(
		repoAuth: RepositoryAuthImpl
	): RepositoryAuth

	@Binds
	abstract fun bindRepositoryFeeds(
		repoFeeds: RepositoryFeedsImpl
	): RepositoryFeeds
}