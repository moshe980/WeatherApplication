package com.giniapps.weatherapplication.di

import android.content.Context
import androidx.room.Room
import com.giniapps.weatherapplication.database.WeatherDb
import com.giniapps.weatherapplication.database.logic.DataSource
import com.giniapps.weatherapplication.database.logic.DataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    private val dbName = "WeatherDB"

    @Provides
    fun provideDataResource(@ApplicationContext context: Context): DataSource {
        val db = Room.databaseBuilder(context, WeatherDb::class.java, dbName)
            .fallbackToDestructiveMigration()
            .build()

        return DataSourceImpl(db.weatherDao())
    }
}