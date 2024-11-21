package com.test.weatherapp1.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Response body for GetAccounts API for vendor users
 */

@Parcelize
data class ResponseGetNumberFact(
    val numberFact: String,
) : Parcelable