package com.test.weatherapp1.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.test.weatherapp1.R
import com.test.weatherapp1.data.WeatherUiState
import com.test.weatherapp1.data.model.GetWeatherRequest
import com.test.weatherapp1.data.model.GetWeatherResponse
import com.test.weatherapp1.data.model.HourItem
import com.test.weatherapp1.data.network.Result
import com.test.weatherapp1.data.repository.NetworkRepository
import com.test.weatherapp1.util.Event
import com.test.weatherapp1.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // Entered city name
    val citySearchText = MutableLiveData<String>()

    // LiveData for showCityFindError
    private val _showCityFindError = MutableLiveData<Event<Unit>>()
    val showCityFindError: LiveData<Event<Unit>> = _showCityFindError

    // LiveData for showCityFindByNameError
    private val _showCityFindByNameError = MutableLiveData<Event<Unit>>()
    val showCityFindByNameError: LiveData<Event<Unit>> = _showCityFindByNameError

    // LiveData for network error
    private val _showNetworkError = MutableLiveData<Event<Unit>>()
    val showNetworkError: LiveData<Event<Unit>> = _showNetworkError

    // LiveData for navigateToHomeFragment
    private val _navigateToHomeFragment = SingleLiveEvent<Event<Unit>>()
    val navigateToHomeFragment: LiveData<Event<Unit>> = _navigateToHomeFragment

    // LiveData for update current weather icon on HomeFragment
    private val _getWeatherListResult = MutableLiveData<List<HourItem>>()
    val getWeatherListResult: LiveData<List<HourItem>> = _getWeatherListResult

    init {
        citySearchText.value = ""
    }

    /** Check location permissions */
    fun isLocationPermissionGranted(activity: Activity): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            activity, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    /** Request location permissions */
    fun requestLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )
    }

    /** Get current device location with high accuracy */
    @SuppressLint("MissingPermission")
    fun getLocation(enabledLocation: () -> Unit, emptyLocation: () -> Unit) {
        val cancellationTokenSource = CancellationTokenSource()

        // Get current location
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_LOW_POWER, cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->

            // Got current location. If location disabled, will be null
            if (location != null) {
                _uiState.update { currentState ->
                    currentState.copy(lat = location.latitude, lon = location.longitude)
                }
                enabledLocation.invoke()
                cancellationTokenSource.cancel()
            } else {
                emptyLocation.invoke()
            }
        }
    }


    /**
     * Retrieves the city name from the provided geographic coordinates (latitude and longitude).
     *
     * This function uses the `Geocoder` class to convert latitude and longitude into a city name
     * and updates the UI state with the city name, latitude, and longitude. In case of an error,
     * an error message is posted to notify the user.
     *
     * @param context The application context used to create the `Geocoder` instance.
     * @param latitude The latitude of the location to fetch the city name for.
     * @param longitude The longitude of the location to fetch the city name for.
     */
    fun getCityFromCoordinates(context: Context, latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            // Make a request to fetch the address from latitude and longitude.
            geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    // Update the UI state with the city, latitude, and longitude.
                    _uiState.update { currentState ->
                        currentState.copy(
                            lon = longitude, lat = latitude, city = addresses[0].locality
                        )
                    }
                    // Post an event to navigate to the home fragment.
                    _navigateToHomeFragment.postValue(Event(Unit))
                }

                override fun onError(errorMessage: String?) {
                    super.onError(errorMessage)
                    // Post an error event to show an error to the user.
                    _showCityFindError.postValue(Event(Unit))
                }

            })

        } catch (e: Exception) {
            Log.e("GeocoderError", "Error fetching city: ${e.message}")
            // Post an error event to show an error to the user.
            _showCityFindError.postValue(Event(Unit))
        }
    }

    /**
     * Retrieves the geographic coordinates (latitude and longitude) from a city name.
     *
     * This function uses the `Geocoder` class to convert a city name into geographic coordinates.
     * If the city is found, it updates the UI state with the coordinates and city name.
     * In case of an error or if the city cannot be found, an error event is triggered.
     *
     * @param context The application context used to create the `Geocoder` instance.
     * @param cityName The name of the city to fetch coordinates for.
     */
    fun getCoordinatesFromCityName(context: Context, cityName: String) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            // Make a request to fetch the latitude and longitude for the given city name.
            geocoder.getFromLocationName(cityName, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isNotEmpty()) {
                        // If addresses are found, use the first one to get latitude and longitude.
                        val address = addresses[0]

                        // Update the UI state with the found coordinates and city name.
                        _uiState.update { currentState ->
                            currentState.copy(
                                lat = address.latitude, lon = address.longitude, city = cityName
                            )
                        }
                        // Post an event to navigate to the home fragment after successful retrieval.
                        _navigateToHomeFragment.postValue(Event(Unit))
                    } else {
                        // If no addresses are found, post an error event.
                        _showCityFindByNameError.postValue(Event(Unit))
                    }
                }

                override fun onError(errorMessage: String?) {
                    super.onError(errorMessage)
                    // If no addresses are found, post an error event.
                    _showCityFindByNameError.postValue(Event(Unit))
                }

            })
        } catch (e: Exception) {
            Log.e("GeocoderError", "Error fetching coordinates: ${e.message}")
            // If no addresses are found, post an error event.
            _showCityFindByNameError.postValue(Event(Unit))
        }
    }


    /**
     * Fetches the current weather data for the location specified in the UI state and updates the UI.
     *
     * This function launches a coroutine in the `viewModelScope` to make a network request to fetch
     * the current weather data using the `networkRepository`. The weather data is then parsed and
     * the UI state is updated with the current temperature, maximum temperature, minimum temperature,
     * humidity, wind speed, and pressure. If an error occurs, an error event is posted to notify the user.
     *
     * @param context The application context, used to fetch localized string resources
     */
    fun getCurrentWeatherData(context: Context) {
        viewModelScope.launch {
            // Make a network request to get the current weather data.
            networkRepository.getCurrentWeather(
                GetWeatherRequest(
                    latitude = uiState.value.lat, longitude = uiState.value.lon
                )
            ).let { result ->
                when (result) {
                    is Result.Success<GetWeatherResponse> -> {
                        // Extract the first data period (the current weather period).
                        val currentDataPeriod = result.data.list[0]

                        // Convert the temperature to a rounded integer and format it.
                        val currentTemp = currentDataPeriod.main.temp.roundToInt().toString() + "º"

                        // Format the maximum temperature using a string resource.
                        val maxTemp = String.format(
                            context.getString(R.string.max_temp),
                            currentDataPeriod.main.tempMax.roundToInt().toString()
                        )

                        // Format the minimum temperature using a string resource.\
                        val minTemp = String.format(
                            context.getString(R.string.min_temp),
                            currentDataPeriod.main.tempMin.roundToInt().toString()
                        )

                        // Get the humidity and format it with a "%" symbol.
                        val humidity = currentDataPeriod.main.humidity.toString() + "%"
                        // Get the wind speed and format it in km/h.
                        val windy = currentDataPeriod.wind.speed.roundToInt().toString() + "km/h"

                        // Update the UI state with the parsed weather data.
                        _uiState.update { currentData ->
                            currentData.copy(
                                currentTemp = currentTemp,
                                maxTemp = maxTemp,
                                minTemp = minTemp,
                                humidity = humidity,
                                pressure = currentDataPeriod.main.pressure.toString(),
                                windy = windy,
                                imageLink = currentDataPeriod.weather[0].icon
                            )
                        }
                        // Post the weather data list to `_getWeatherListResult`.
                        _getWeatherListResult.postValue(result.data.list)
                    }

                    is Result.Error -> {
                        // Post the weather data list to `_getWeatherListResult`.
                        _showNetworkError.postValue(Event(Unit))
                    }
                    // Default case for any unexpected result (no action).
                    else -> Unit
                }
            }
        }
    }

    /** Get today date in next format "Aug, 05" */
    fun getCurrentFormattedDate() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM, dd", Locale.ENGLISH)

        _uiState.update { currentData ->
            currentData.copy(currentDay = dateFormat.format(calendar.time))
        }
    }
}