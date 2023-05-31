package com.dmm.rssreader.di

import com.dmm.rssreader.data.network.apis.*
import com.dmm.rssreader.utils.Constants.ANDROID_DEVELOPERS
import com.dmm.rssreader.utils.Constants.DANLEW_BLOG
import com.dmm.rssreader.utils.Constants.DEVELOPER_ANDROID_BLOG
import com.dmm.rssreader.utils.Constants.DEVELOPER_MEDIUM
import com.dmm.rssreader.utils.Constants.KOTLIN_WEEKLY
import com.dmm.rssreader.utils.HostSelectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun provideRetrofitApi(okHttpClient: OkHttpClient): Retrofit {
		return Retrofit.Builder()
			.baseUrl("https://my.fake.url/")
			.client(okHttpClient)
			.addConverterFactory(ScalarsConverterFactory.create())
			.build()
	}

	@Provides
	@Singleton
	fun provideApiService(retrofit: Retrofit): ApiService {
		return retrofit.create(ApiService::class.java)
	}

	@Provides
	@Singleton
	fun provideHostSelectionInterceptor(): HostSelectionInterceptor {
		return HostSelectionInterceptor()
	}

	@Provides
	@Singleton
	fun provideOkHttpClient(hostSelectionInterceptor: HostSelectionInterceptor?): OkHttpClient {
		return OkHttpClient().newBuilder()
			.retryOnConnectionFailure(true)
			.followRedirects(true)
			.followSslRedirects(true)
			.addInterceptor(hostSelectionInterceptor!!)
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.build()
	}
}