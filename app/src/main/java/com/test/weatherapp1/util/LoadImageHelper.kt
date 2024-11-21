package com.test.weatherapp1.util

import android.widget.ImageView
import coil.load
import com.test.weatherapp1.R

const val ICON_URL = "https://openweathermap.org/img/wn/"

/**
 * A helper object to load weather icons into an `ImageView` using an image URL.
 *
 * This object provides a utility method to load an icon image from the OpenWeatherMap icon URL base
 * and displays it in an `ImageView`. It includes error and placeholder handling for cases where
 * the image fails to load or is not yet available.
 */
object LoadImageHelper {

    fun loadIconToImageView(imageLink: String, imageView: ImageView) {
        val iconUrl = "$ICON_URL$imageLink@2x.png"

        // Load the image into the ImageView using the Coil image loading library.
        imageView.load(iconUrl) {
            // Display a placeholder image while the icon is loading.
            placeholder(R.drawable.ic_cloud)
            // Display an error image if the icon fails to load.
            error(R.drawable.ic_cloud)
            // Enable crossfade animation when the image is loaded.
            crossfade(true)
        }
    }
}