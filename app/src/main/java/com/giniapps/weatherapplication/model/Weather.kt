package com.giniapps.weatherapplication.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "WEATHERS")
@Parcelize
data class Weather(
    @PrimaryKey
    val id: Int = 101,
    val icon: String,
    val lat: Double,
    val lng: Double,
    val summary: String,
    var temperature: Double,
    var address:String?="",
) : Parcelable {
    fun getImageUrl() =
        "$AMBEEDATA_IMAGE_URL$icon.png"


    companion object {
        const val AMBEEDATA_IMAGE_URL =
            "https://assetambee.s3-us-west-2.amazonaws.com/weatherIcons/PNG/"
    }
}