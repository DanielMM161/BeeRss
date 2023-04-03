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
	@Named("AndroidBlogs")
	fun provideRetrofitAndroidBlogs(okHttpClient: OkHttpClient): Retrofit {
		return getRetrofitInstance(DEVELOPER_ANDROID_BLOG, okHttpClient)
	}

	@Provides
	@Singleton
	@Named("AndroidMedium")
	fun provideRetrofitAndroidMedium(okHttpClient: OkHttpClient): Retrofit {
		return getRetrofitInstance(DEVELOPER_MEDIUM, okHttpClient)
	}

	@Provides
	@Singleton
	@Named("AndroidDevelopers")
	fun provideRetrofitAndroidDevelopers(okHttpClient: OkHttpClient): Retrofit {
		return getRetrofitInstance(ANDROID_DEVELOPERS, okHttpClient)
	}

	@Provides
	@Singleton
	@Named("KotlinWeekly")
	fun provideRetrofitKotlinWeekly(okHttpClient: OkHttpClient): Retrofit {
		return getRetrofitInstance(KOTLIN_WEEKLY, okHttpClient)
	}

	@Provides
	@Singleton
	@Named("DanLew")
	fun provideRetrofitDanLew(okHttpClient: OkHttpClient): Retrofit {
		return getRetrofitInstance(DANLEW_BLOG, okHttpClient)
	}

	@Provides
	@Singleton
	fun provideAndroidBlogsApi(@Named("AndroidBlogs") retrofit: Retrofit): ServiceAndroidBlogs {
		return retrofit.create(ServiceAndroidBlogs::class.java)
	}

	@Provides
	@Singleton
	fun provideAndroidMediumApi(@Named("AndroidMedium") retrofit: Retrofit): ServiceDevsMedium {
		return retrofit.create(ServiceDevsMedium::class.java)
	}

	@Provides
	@Singleton
	fun provideAndroidDevelopersApi(@Named("AndroidDevelopers") retrofit: Retrofit): ServiceAndroidDevelopers {
		return retrofit.create(ServiceAndroidDevelopers::class.java)
	}

	@Provides
	@Singleton
	fun provideKotlinWeeklyApi(@Named("KotlinWeekly") retrofit: Retrofit): ServiceKotlinWeekly {
		return retrofit.create(ServiceKotlinWeekly::class.java)
	}

	@Provides
	@Singleton
	fun provideDanLewApi(@Named("DanLew") retrofit: Retrofit): ServiceDanLew {
		return retrofit.create(ServiceDanLew::class.java)
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

	private fun getRetrofitInstance(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
		return Retrofit.Builder()
			.baseUrl(baseUrl)
			.client(okHttpClient)
			.addConverterFactory(ScalarsConverterFactory.create())
			.build()
	}
}