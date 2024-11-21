package com.test.weatherapp1.data.repository

import com.test.weatherapp1.BuildConfig
import com.test.weatherapp1.data.api.NetworkService
import com.test.weatherapp1.data.model.GetWeatherRequest
import com.test.weatherapp1.data.model.GetWeatherResponse
import com.test.weatherapp1.data.network.RestAPIClient
import com.test.weatherapp1.data.network.Result

const val UNITS = "metric"

/**
 * Implementation of the `NetworkRepository` interface, responsible for
 * fetching weather data from the network via the `NetworkService`.
 *
 * @property networkService The Retrofit service for making network calls.
 */
class NetworkRepositoryImpl(
    private val networkService: NetworkService
) : NetworkRepository {

    /**
     * Fetches the current weather data using the provided request parameters.
     *
     * This function wraps the network call in a `Result` to encapsulate success or failure,
     * leveraging a generic API call handler provided by `RestAPIClient.callAPI`.
     *
     * @param getWeatherRequest The request containing the necessary parameters for the weather API.
     * @return A `Result` containing a successful response (`GetWeatherResponse`) or an error.
     */
    override suspend fun getCurrentWeather(
        getWeatherRequest: GetWeatherRequest
    ): Result<GetWeatherResponse> {
        return RestAPIClient.callAPI("getNumberFact") {
            networkService.getCurrentWeather(
                latitude = getWeatherRequest.latitude,
                longitude = getWeatherRequest.longitude,
                units = UNITS,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
        }
    }
}
