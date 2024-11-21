package com.test.weatherapp1.data.model

import com.google.gson.annotations.SerializedName

/** Hour weather info class */
data class HourItem(
    val clouds: Clouds,
    val dt: Int,
    @SerializedName("dt_txt")
    val dtTxt: String,
    val main: Main,
    val pop: Double,
    val rain: Rain,
    val snow: Snow,
    val sys: Sys,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
) {
    data class Clouds(
        val all: Int
    )

    data class Main(
        val feelsLike: Double,
        val grndLevel: Int,
        val humidity: Int,
        val pressure: Int,
        val seaLevel: Int,
        val temp: Double,
        val tempKf: Double,
        @SerializedName("temp_max")
        val tempMax: Double,
        @SerializedName("temp_min")
        val tempMin: Double
    )

    data class Rain(
        @SerializedName("3h")
        val h: Double
    )

    data class Snow(
        @SerializedName("3h")
        val h: Double
    )

    data class Sys(
        val pod: String
    )

    data class Weather(
        val description: String,
        val icon: String,
        val id: Int,
        val main: String
    )

    data class Wind(
        val deg: Int,
        val gust: Double,
        val speed: Double
    )
}
