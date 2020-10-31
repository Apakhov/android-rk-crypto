package com.boww.crypto

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.boww.crypto.databinding.DailyPriceItemBinding
import java.util.*
import kotlin.time.ExperimentalTime

class DailyPriceAdapter(
    private val context: Context,
    private val dailyPriceItems: List<DailyPriceViewModel.DailyPriceItem>
)
    : RecyclerView.Adapter<DailyPriceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(DailyPriceItemBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = dailyPriceItems.size

    @ExperimentalTime
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dailyPriceItems[position])
    }

    inner class ViewHolder(private val dailyPriceItemBinding: DailyPriceItemBinding)
        : RecyclerView.ViewHolder(dailyPriceItemBinding.root) {

        @ExperimentalTime
        fun bind(item: DailyPriceViewModel.DailyPriceItem) {
            val dayBegin = beginOfDay(item.ts.toDate())
            dailyPriceItemBinding.day = dayBegin.toString(DAY_PATTERN, Locale.getDefault())
            dailyPriceItemBinding.monthYear = dayBegin.toString(MONTH_YEAR_PATTERN, Locale.getDefault()).toUpperCase(Locale.getDefault())
            dailyPriceItemBinding.open = item.open.significant(9, 2)
            dailyPriceItemBinding.close = item.close.significant(9, 2)
            dailyPriceItemBinding.change = (item.close - item.open).significantWithSign(6, 2)

            val stonksColor =
                if (item.close - item.open > 0) ContextCompat.getColor(context, R.color.colorStonks)
                else ContextCompat.getColor(context, R.color.colorNotStonks)
            dailyPriceItemBinding.dailyChangeValue.setTextColor(stonksColor)

            dailyPriceItemBinding.root.setOnClickListener { v ->
                Log.i(TAG, "Click on item: ${dailyPriceItemBinding.day} ${dailyPriceItemBinding.monthYear}")
                val bundle = bundleOf(
                    DayOverviewFragment.ARG_BEGIN_OF_DAY to dayBegin.ts(),
                )
                v.findNavController().navigate(R.id.action_dailyPriceListFragment_to_dayOverviewFragment, bundle)
            }

            Log.i(TAG, "Bind done. Item: ${dailyPriceItemBinding.day} ${dailyPriceItemBinding.monthYear}")
        }
    }

    companion object {
        private const val TAG = "DailyPriceAdapter"
        private const val DAY_PATTERN = "dd"
        private const val MONTH_YEAR_PATTERN = "MMM yyyy"
    }
}