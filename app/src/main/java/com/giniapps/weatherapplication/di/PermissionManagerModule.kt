package com.giniapps.weatherapplication.di

import com.giniapps.weatherapplication.permission.PermissionManagerImp
import com.giniapps.weatherapplication.permission.PermissionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionManagerModule {

    @Binds
    @Singleton
    abstract fun bindPermissionManager(permissionManager: PermissionManagerImp): PermissionManager
}