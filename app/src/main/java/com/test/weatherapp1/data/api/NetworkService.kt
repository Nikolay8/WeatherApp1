package com.test.weatherapp1.data.api

import com.test.weatherapp1.data.model.GetWeatherResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface defining the network service for interacting with the weather API.
 * Uses Retrofit to handle HTTP requests and responses.
 */
interface NetworkService {

    /**
     * Fetches the current weather forecast based on geographic coordinates.
     *
     * @param latitude The latitude of the location for the weather forecast.
     * @param longitude The longitude of the location for the weather forecast.
     * @param units The unit system to use for the weather data (e.g., "metric" for Celsius, "imperial" for Fahrenheit).
     * @param apiKey The API key for authenticating with the weather service.
     * @return A `Response` object containing a `GetWeatherResponse` if the request is successful.
     */
    @GET("data/2.5/forecast")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Response<GetWeatherResponse>

}