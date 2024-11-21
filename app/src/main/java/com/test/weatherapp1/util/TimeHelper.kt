package com.test.weatherapp1.util

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeHelper {

    /** API time string "2024-11-20 21:00:00", return HH:mm */
    fun getHourAndMinuteFromTimeStamp(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            return ""
        }

        try {
            // Inner format
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            // Output format
            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
            // Parsing by LocalDateTime
            val dateTime = LocalDateTime.parse(dateString, inputFormatter)

            return dateTime.format(outputFormatter)
        } catch (e: Exception) {
            Log.e("TimeHelperError", "Error in parsing getHourAndMinuteFromTimeStamp: ${e.message}")
            return ""
        }
    }

    /** API time string "2024-11-20 21:00:00", converting to string day of week like "Monday" */
    fun getDayOfWeekString(dateString: String): String {
        // Inner format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        // Parsing by LocalDateTime
        val dateTime = LocalDateTime.parse(dateString, formatter)

        return dateTime.dayOfWeek.name.lowercase(Locale.getDefault())
            .replaceFirstChar { it.uppercase() }
    }
}