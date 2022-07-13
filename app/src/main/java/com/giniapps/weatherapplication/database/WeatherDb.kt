package com.giniapps.weatherapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.giniapps.weatherapplication.database.dal.WeatherDao
import com.giniapps.weatherapplication.model.Weather

@Database(
    entities = [
        Weather::class,

    ],
    version = 1, exportSchema = false
)
abstract class WeatherDb : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}