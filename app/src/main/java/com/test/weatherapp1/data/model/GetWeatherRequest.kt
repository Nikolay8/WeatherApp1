package com.test.weatherapp1.data.model

import com.squareup.moshi.JsonClass

/**
 * Request body for get weather data
 */
@JsonClass(generateAdapter = true)
data class GetWeatherRequest(
    val latitude: Double,
    val longitude: Double,
)
