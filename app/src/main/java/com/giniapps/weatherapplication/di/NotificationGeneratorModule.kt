package com.giniapps.myboundservice.di

import com.giniapps.mylocation4.notification.NotificationGenerator
import com.giniapps.mylocation4.notification.NotificationGeneratorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationGeneratorModule {

    @Binds
    @Singleton
    abstract fun bindNotificationGenerator(notificationGenerator: NotificationGeneratorImpl): NotificationGenerator
}