package com.test.weatherapp1.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.weatherapp1.data.model.HourItem
import com.test.weatherapp1.databinding.ItemForecastWeatherBinding
import com.test.weatherapp1.util.LoadImageHelper
import com.test.weatherapp1.util.TimeHelper
import kotlin.math.roundToInt

/**
 * Combined adapter for using with forecast weather info
 */
class ForecastWeatherItemAdapter(val context: Context) :

    ListAdapter<HourItem, ForecastWeatherItemAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemForecastWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemForecastWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hourItem: HourItem) = with(binding) {

            binding.dayOfWeekTextView.text = TimeHelper.getDayOfWeekString(hourItem.dtTxt)
            LoadImageHelper.loadIconToImageView(
                imageLink = hourItem.weather[0].icon,
                binding.statusImageView
            )

            val tempMaxString = hourItem.main.tempMax.roundToInt().toString()
            val tempMinString = hourItem.main.tempMin.roundToInt().toString()
            val tempString = tempMaxString + "ºC " + tempMinString + "ºC"
            binding.tempTextView.text = tempString
        }
    }

    /**
     * Callback for calculating the diff between two non-null items in a list.
     */
    class DiffCallback : DiffUtil.ItemCallback<HourItem>() {
        override fun areItemsTheSame(
            oldItem: HourItem, newItem: HourItem
        ): Boolean {
            return oldItem.dtTxt == newItem.dtTxt && oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(
            oldItem: HourItem, newItem: HourItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
