package com.giniapps.weatherapplication.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.giniapps.weatherapplication.networking.RemoteApi
import com.giniapps.weatherapplication.networking.RemoteApiImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteApiModule {

    @Binds
    @Singleton
    abstract fun bindRemoteApi(remoteApi: RemoteApiImpl): RemoteApi
}