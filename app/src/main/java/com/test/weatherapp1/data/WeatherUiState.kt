package com.test.weatherapp1.data


/**
 * Data class that represents the current UI state
 */
data class WeatherUiState(

    /** User name */
    val name: String = "",

    /** User email */
    val email: String = "",

    /** Location */
    val lat: Double = 0.0, val lon: Double = 0.0,

    /** Location city */
    val city: String = "",

    /** Current day temperature */
    val currentTemp: String = "",

    /** Max day temperature */
    val maxTemp: String = "",

    /** Min day temperature */
    val minTemp: String = "",

    /** Day humidity */
    val humidity: String = "",

    /** Day pressure */
    val pressure: String = "",

    /** Day wind */
    val windy: String = "",

    /** Current day */
    val currentDay: String = "",

    /** Current day image link */
    val imageLink: String = ""
)
