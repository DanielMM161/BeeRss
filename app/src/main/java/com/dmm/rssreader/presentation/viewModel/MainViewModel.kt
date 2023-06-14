package com.dmm.rssreader.presentation.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dmm.rssreader.MainApplication
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.domain.model.Source
import com.dmm.rssreader.domain.model.UserProfile
import com.dmm.rssreader.domain.repositories.RepositoryAuth
import com.dmm.rssreader.domain.repositories.RepositoryFeeds
import com.dmm.rssreader.domain.repositories.RepositorySource
import com.dmm.rssreader.domain.repositories.RepositoryUser
import com.dmm.rssreader.domain.usecase.*
import com.dmm.rssreader.utils.Constants
import com.dmm.rssreader.utils.Constants.THEME_DAY
import com.dmm.rssreader.utils.Constants.THEME_NIGHT
import com.dmm.rssreader.utils.Resource
import com.dmm.rssreader.utils.Utils
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainViewModel @Inject constructor(
	app: Application,
	private val repoFeeds: RepositoryFeeds,
	private val repoUser: RepositoryUser,
	private val repoSource: RepositorySource,
	private val repoAuth: RepositoryAuth,
	private val firebaseAnalytics: FirebaseAnalytics
) : AndroidViewModel(app) {

	lateinit var userProfile: UserProfile
	var sources: List<Source> = listOf()
	private var _developerFeeds = MutableStateFlow<Resource<List<FeedUI>?>>(Resource.Loading())
	val developerFeeds = _developerFeeds.asStateFlow()

	private var _favouritesFeeds = MutableStateFlow<List<FeedUI>>(mutableListOf())
	val favouritesFeeds = _favouritesFeeds.asStateFlow()

	var searchText: String = ""

	init {
		viewModelScope.launch {
			repoSource.fetchSources().collect { result ->
				sources = result
				fetchFeedsDeveloper()
			}
		}
	}

	fun userProfileInitialized(): Boolean {
		return this::userProfile.isInitialized
	}

	suspend fun fetchFeed(source: Source): List<FeedUI> {
		return repoFeeds.fetchFeeds(source.baseUrl, source.route, source.title)
	}

	fun fetchFeedsDeveloper() = viewModelScope.launch {
		Log.e("fetchFeedsDeveloper --> ", "fetchFeedsDeveloper")
		val listFeed: MutableList<FeedUI> = mutableListOf()

		userProfile.sources.forEach { source ->
			val result = sortedFeed(fetchFeed(source))
			listFeed += result

		}
		_developerFeeds.value = Resource.Success(listFeed)
	}

	fun findFeeds(text: String): List<FeedUI>? {
		return _developerFeeds.value.data?.filter {
			it.title.lowercase().contains(text.lowercase())
		}
	}

	fun setUserSources(source: Source) {
		if (userProfile.sources.contains(source)) {
			userProfile.sources.remove(source)
		} else {
			userProfile.sources.add(source)
		}
	}

	suspend fun <T> updateUser(data: T, property: String): Resource<Boolean> {
		return repoUser.updateUser(userProfile.email, data, property )
	}

	/**
	 * Save the favourite feed in a local way and remote way as well setting it in the
	 * user profile when is remote
	 */
	fun saveFavouriteFeed(feedSelected: FeedUI, callback: () -> Unit = {}) = viewModelScope.launch {
		setFavouriteUserFeed(feedSelected)
		updateUser(userProfile.favouritesFeeds, "favouriteFeeds")

		feedSelected.favourite = !feedSelected.favourite
		repoFeeds.updateFeedLocal(feedSelected.favourite, feedSelected.title)
		callback()
	}

	private fun setFavouriteUserFeed(feedSelected: FeedUI) {
		if (userProfile.favouritesFeeds.contains(feedSelected))
			userProfile.favouritesFeeds.remove(feedSelected)
		else
			userProfile.favouritesFeeds.add(feedSelected.copy(favourite = true))

	}

	fun getFavouriteFeeds() = viewModelScope.launch {
		_favouritesFeeds.value = repoFeeds.getFavouriteFeedsLocal()
	}

	private fun sortedFeed(feeds: List<FeedUI>): List<FeedUI> {
		val dateEmptyList = feeds.filter { it.published.isEmpty() }
		val sortedFeeds = feeds.filter { it.published.isNotEmpty() }
			.sortedByDescending { LocalDate.parse(it.published, DateTimeFormatter.ofPattern(Constants.DATE_PATTERN_OUTPUT)) }

		return Utils.merge(sortedFeeds, dateEmptyList).toList()
	}

	fun autoSelectedTheme() {
		when (userProfile.theme) {
			THEME_DAY -> {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			}
			THEME_NIGHT -> {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			}
		}
	}

	fun signOut() {
		repoAuth.signOut()
	}

	suspend fun deleteTable() {
		repoFeeds.deleteTable()
		Log.e("DELETING TABLE --> ", "DELETING")
	}

	fun logSelectItem(value: String) {
		val params = Bundle()
		params.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, value)
		firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
	}

	fun logShare(contentType: String, itemId: String) {
		val params = Bundle()
		params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
		params.putString(FirebaseAnalytics.Param.ITEM_ID, itemId)
		firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, params)
	}

	private fun hasInternetConnection(): Boolean {
		val connectivityManager = getApplication<MainApplication>().getSystemService(
			Context.CONNECTIVITY_SERVICE
		) as ConnectivityManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val activeNetwork = connectivityManager.activeNetwork ?: return false
			val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
			return when {
				capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
				capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
				capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
				else -> return false
			}
		} else {
			connectivityManager.activeNetworkInfo?.run {
				return when (type) {
					ConnectivityManager.TYPE_WIFI -> return true
					ConnectivityManager.TYPE_MOBILE -> return true
					ConnectivityManager.TYPE_ETHERNET -> return true
					else -> return false
				}
			}
		}
		return false
	}
}