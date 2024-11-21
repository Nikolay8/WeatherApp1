package com.test.weatherapp1.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.weatherapp1.R
import com.test.weatherapp1.data.model.HourItem
import com.test.weatherapp1.databinding.ItemHourWeatherBinding
import com.test.weatherapp1.util.LoadImageHelper
import com.test.weatherapp1.util.TimeHelper
import kotlin.math.roundToInt

/**
 * Combined adapter for using with hour weather info
 */
class HourWeatherItemAdapter(val context: Context) :

    ListAdapter<HourItem, HourWeatherItemAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemHourWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(private val binding: ItemHourWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hourItem: HourItem, position: Int) = with(binding) {

            // Change background for current time line
            if (position == 1) {
                binding.itemHourWeatherMainLayout.background = ContextCompat.getDrawable(
                    context, R.drawable.rounded_home_background_with_selection
                )
            } else {
                binding.itemHourWeatherMainLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.rounded_home_background)
            }

            val tempString = hourItem.main.temp.roundToInt().toString() + "ºC"
            binding.temperatureTextView.text = tempString
            LoadImageHelper.loadIconToImageView(
                imageLink = hourItem.weather[0].icon, binding.statusImageView
            )
            binding.hourTemperatureTextView.text =
                TimeHelper.getHourAndMinuteFromTimeStamp(hourItem.dtTxt)
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
