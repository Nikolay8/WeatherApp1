package com.test.weatherapp1.data.network

import okhttp3.Request
import retrofit2.Response

/**
 * Generic REST API Client that can understand various types of API errors and can handle them all
 * at one place and just return the [Result] wrapper indicating Success or Error states with all the
 * information about the error/exception of the successful response
 */
object RestAPIClient {

    const val TAG = "RestApiClient"

    private const val EMPTY_RESPONSE = 204

    const val CODE_REQUEST_ABORTED_BY_USER = 1
    const val MESSAGE_REQUEST_ABORTED_BY_USER = "Request aborted by user"
    const val CODE_REQUEST_ENCOUNTERED_EXCEPTION = 2
    const val MESSAGE_REQUEST_ENCOUNTERED_EXCEPTION = "Request encountered an exception"
    const val CODE_REQUEST_FAILED_WITH_NOTIFICATION = 3
    const val MESSAGE_REQUEST_FAILED_WITH_NOTIFICATION = "Request failed with api notification"

    /**
     * Parsing Error Analytics to be handled here
     *
     * Analytics about general API errors should be handled through OkHttp Interceptors
     */
    private const val API_RESPONSE_PARSING_ERROR = "API_RESPONSE_PARSING_ERROR"
    private const val ATTRIBUTE_PATH = "path"
    private const val PATH_UNKNOWN = "unknown"

    /**
     * Calls the API and converts the response to [Result] while also performing the generic error
     * handling
     *
     * apiIdentifier is just for logging, use any name that helps identify what API is being called
     */
    suspend fun <T> callAPI(apiIdentifier: String, apiCall: suspend () -> Response<T>): Result<T> {
        var request: Request? = null
        var responseCode = 0
        return try {
            val response = apiCall()
            responseCode = response.code()
            if (response.isSuccessful) {
                val data = response.body()
                if (data == null || responseCode == EMPTY_RESPONSE) {
                    Result.SuccessEmptyResponse
                } else {
                    Result.Success(data)
                }
            } else if (responseCode == CODE_REQUEST_ABORTED_BY_USER) {
                Result.Aborted
            } else {
                // Error is always wrapped into a [ResponseException]
                Result.Error(Exception())
            }
        } catch (throwable: Throwable) {
            // add to log
            Result.Error(Exception())
        }
    }
}