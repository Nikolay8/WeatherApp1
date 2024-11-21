package com.test.weatherapp1.data.repository


import com.test.weatherapp1.data.model.GetWeatherRequest
import com.test.weatherapp1.data.model.GetWeatherResponse
import com.test.weatherapp1.data.network.Result

/**
 *  Repository for network functional
 */
interface NetworkRepository {

    suspend fun getCurrentWeather(getWeatherRequest: GetWeatherRequest): Result<GetWeatherResponse>

}
