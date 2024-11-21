package com.test.weatherapp1.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.test.weatherapp1.R
import com.test.weatherapp1.databinding.FragmentSelectLocationBinding
import com.test.weatherapp1.ui.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectLocationFragment : Fragment() {

    private val viewModel: WeatherViewModel by activityViewModels()

    private var _binding: FragmentSelectLocationBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSelectLocationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.fragment = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check location permissions
        val isLocationPermissionGranted =
            activity?.let { viewModel.isLocationPermissionGranted(activity = it) }

        if (isLocationPermissionGranted != null && !isLocationPermissionGranted) {
            activity?.let { viewModel.requestLocationPermissions(it) }
        }

        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        // Error showCityFindError
        viewModel.showCityFindError.observe(viewLifecycleOwner) {
            showCityFindError()
        }

        // Error showCityFindByNameError
        viewModel.showCityFindByNameError.observe(viewLifecycleOwner) {
            showCityFindByNameError()
        }

        // Navigate to HomeFragment event
        viewModel.navigateToHomeFragment.observe(viewLifecycleOwner) {
            navigateToHomeFragment()
        }
    }

    private fun showCityFindError() {
        Toast.makeText(
            requireActivity(),
            getString(R.string.can_not_find_city_by_your_location), Toast.LENGTH_LONG
        )
            .show()
    }

    private fun showCityFindByNameError() {
        Toast.makeText(
            requireActivity(),
            getString(R.string.please_enter_valid_city_name), Toast.LENGTH_LONG
        ).show()
    }

    // On get location click
    fun getLocationCLick(view: View) {
        viewModel.getLocation(enabledLocation = {
            // For success get location
            viewModel.getCityFromCoordinates(
                context = requireContext(),
                latitude = viewModel.uiState.value.lat,
                longitude = viewModel.uiState.value.lon
            )
        }, emptyLocation = {
            // Show alert dialog with new enabling Location in settings
            val alertDialog =
                AlertDialog.Builder(context).setTitle(getString(R.string.location_required))
                    .setMessage(getString(R.string.you_need_to_enable_location_tracking_in_settings))
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->

                        // Enable location in settings
                        enableLocationOnDevice()
                        dialog.dismiss()
                    }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }.create()
            alertDialog.show()
        })
    }

    // On get location from city name click
    fun getCoordinatesByCityNameClick(view: View) {
        val citySearchText = viewModel.citySearchText.value

        if (citySearchText.isNullOrEmpty()) {
            showCityFindByNameError()
        } else {
            viewModel.getCoordinatesFromCityName(requireContext(), citySearchText)
        }
    }

    /**
     * Opens the device's location settings to enable location services.
     *
     * This function launches an intent to the system's location settings page,
     * allowing the user to manually enable location services. It uses the
     * `ActivityCompat.startActivityForResult` method to start the activity
     * and listen for the result if needed.
     */
    private fun enableLocationOnDevice() {
        val gpsSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity?.let { ActivityCompat.startActivityForResult(it, gpsSettingIntent, 1, null) }
    }

    // Open home screen
    private fun navigateToHomeFragment() {
        val navController = findNavController()
        navController.navigate(R.id.action_SelectLocationFragment_to_HomeFragment)
    }
}