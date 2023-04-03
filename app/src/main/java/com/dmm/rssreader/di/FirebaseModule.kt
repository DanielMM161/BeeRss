package com.dmm.rssreader.di

import android.content.Context
import com.dmm.rssreader.R
import com.dmm.rssreader.utils.Constants.USERS_COLLECTION
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

	@Provides
	@Singleton
	fun provideFirebaseAuth(): FirebaseAuth {
		return FirebaseAuth.getInstance()
	}

	@Provides
	@Singleton
	fun provideFireStoreInstance(): FirebaseFirestore {
		return FirebaseFirestore.getInstance()
	}

	@Provides
	@Singleton
	fun provideDocumentReference(firestore: FirebaseFirestore): CollectionReference {
		return firestore.collection(USERS_COLLECTION)
	}

	@Provides
	@Singleton
	fun provideGoogleSignInOptions(@ApplicationContext appContext: Context): GoogleSignInOptions {
		return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(appContext.getString(R.string.default_web_client_id))
			.requestEmail()
			.build()
	}

	@Provides
	@Singleton
	fun provideGoogleSignInClient(
		@ApplicationContext appContext: Context,
		googleConf: GoogleSignInOptions
	): GoogleSignInClient {
		return GoogleSignIn.getClient(appContext, googleConf)
	}

	@Provides
	@Singleton
	fun provideFirebaseAnalytics(@ApplicationContext appContext: Context): FirebaseAnalytics {
		return FirebaseAnalytics.getInstance(appContext)
	}
}