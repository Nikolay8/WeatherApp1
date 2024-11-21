package com.test.weatherapp1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.weatherapp1.databinding.FragmentHomeBinding
import com.test.weatherapp1.ui.WeatherViewModel
import com.test.weatherapp1.ui.adapters.ForecastWeatherItemAdapter
import com.test.weatherapp1.ui.adapters.HourWeatherItemAdapter
import com.test.weatherapp1.util.LoadImageHelper
import com.test.weatherapp1.util.TimeHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: WeatherViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var horizontalLinearLayoutManager: LinearLayoutManager? = null
    private var verticalLinearLayoutManager: LinearLayoutManager? = null
    private lateinit var hourAdapter: HourWeatherItemAdapter
    private lateinit var forecastAdapter: ForecastWeatherItemAdapter

    companion object {
        // Time filter value for filter item list
        const val FILTER_TIME = "12:00"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.fragment = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupObservers()
        // Start load data
        viewModel.getCurrentWeatherData(requireContext())
        // Get current day
        viewModel.getCurrentFormattedDate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        // Config recyclerView
        horizontalLinearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        verticalLinearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.layoutManager = horizontalLinearLayoutManager
        binding.forecastRecyclerView.layoutManager = verticalLinearLayoutManager

        hourAdapter = HourWeatherItemAdapter(this.requireContext())
        binding.recyclerView.adapter = hourAdapter

        forecastAdapter = ForecastWeatherItemAdapter(this.requireContext())
        binding.forecastRecyclerView.adapter = forecastAdapter

        // Hide views on start
        binding.forecastInfoBlockLinearLayout.visibility = View.GONE
        binding.homeButton.visibility = View.GONE
    }

    private fun setupObservers() {
        // Network error
        viewModel.showNetworkError.observe(viewLifecycleOwner) {
            showNoInternetConnectionError()
        }

        // Observe getWeather response
        viewModel.getWeatherListResult.observe(viewLifecycleOwner) {
            LoadImageHelper.loadIconToImageView(
                viewModel.uiState.value.imageLink, binding.statusImageView
            )
            val dayItemsList = it.take(4)

            hourAdapter.submitList(dayItemsList)

            // Filter items by FILTER_TIME value
            val forecastList =
                it.filter { hourItem -> TimeHelper.getHourAndMinuteFromTimeStamp(hourItem.dtTxt) == FILTER_TIME }

            forecastAdapter.submitList(forecastList)
        }
    }

    // On Next Forecast button click
    fun onNextForecastClick(view: View) {
        binding.detailInfoBlockLinearLayout.visibility = View.GONE
        binding.forecastInfoBlockLinearLayout.visibility = View.VISIBLE
        binding.nextForecastButton.visibility = View.GONE
        binding.homeButton.visibility = View.VISIBLE
    }

    // On Home button click
    fun onHomeButtonClick(view: View) {
        binding.detailInfoBlockLinearLayout.visibility = View.VISIBLE
        binding.forecastInfoBlockLinearLayout.visibility = View.GONE
        binding.nextForecastButton.visibility = View.VISIBLE
        binding.homeButton.visibility = View.GONE
    }

    private fun showNoInternetConnectionError() {
        Toast.makeText(requireActivity(), "No internet connection", Toast.LENGTH_LONG).show()
    }
}