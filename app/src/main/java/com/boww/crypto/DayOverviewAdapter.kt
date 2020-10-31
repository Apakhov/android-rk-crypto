package com.boww.crypto

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.boww.crypto.databinding.HourPriceItemBinding
import java.util.*

class DayOverviewAdapter(
    private val context: Context,
    private val hourPrices: List<DayOverviewViewModel.HourPriceItem>
) : RecyclerView.Adapter<DayOverviewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(HourPriceItemBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = hourPrices.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(hourPrices[position])
    }

    inner class ViewHolder(private val binding: HourPriceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DayOverviewViewModel.HourPriceItem) {
            binding.hour = Date(item.ts * 1000).toString("HH:mm")

            binding.close = item.close.significant(7, 0)

            val stonksColor = if (item.close - item.open > 0) ContextCompat.getColor(context, R.color.colorStonks)
            else ContextCompat.getColor(context, R.color.colorNotStonks)

            binding.change = (item.close - item.open).significantWithSign(6, 2)
            binding.hourPriceItemChange.setTextColor(stonksColor)
            binding.percent = ((item.close - item.open)/item.open*100).significantWithSign(6, 2)+"%"
            binding.hourPriceItemPercent.setTextColor(stonksColor)

            Log.i(TAG, "Bind done. Item: ${binding.hour}")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DailyPriceListFragment()

        private const val TAG = "HourPriceAdapter"
    }
}