package com.giniapps.mylocation4.di

import android.content.Context
import com.giniapps.weatherapplication.service.SharedLocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
class SharedLocationManagerModule {

    @Provides
    fun provideSharedLocationManager(
        @ApplicationContext context: Context,
        coroutineScope: CoroutineScope
    ): SharedLocationManager =
        SharedLocationManager(context, coroutineScope)
}