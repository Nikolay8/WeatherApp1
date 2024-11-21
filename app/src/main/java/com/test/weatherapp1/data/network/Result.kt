package com.test.weatherapp1.data.network

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    object SuccessEmptyResponse : Result<Nothing>()
    data class Error(val exception: Exception) : Result<Nothing>() {
        fun isNetworkError() = exception.cause is IOException
        fun isParsingError() = exception.cause is JsonParseException
        fun isHttpError() = exception.cause is HttpException
    }

    object Loading : Result<Nothing>()
    object Aborted : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is SuccessEmptyResponse -> "Success EmptyResponse"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading"
            is Aborted -> "Aborted"
        }
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null
