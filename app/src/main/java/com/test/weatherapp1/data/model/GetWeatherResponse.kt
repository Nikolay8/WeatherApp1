package com.test.weatherapp1.data.model


/**
 * Response for get weather data
 */
data class GetWeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<HourItem>,
    val message: Int
) {
    data class City(
        val coord: Coord,
        val country: String,
        val id: Int,
        val name: String,
        val population: Int,
        val sunrise: Int,
        val sunset: Int,
        val timezone: Int
    ) {
        data class Coord(
            val lat: Double,
            val lon: Double
        )
    }
}