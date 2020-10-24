package com.boww.crypto

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.boww.crypto.databinding.HourPriceItemBinding
import java.text.SimpleDateFormat
import java.util.*

class HourPriceAdapter(
    private val context: Context,
    private val hourPrices: List<HourPriceViewModel.HourPriceItemModel>
) : RecyclerView.Adapter<HourPriceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(HourPriceItemBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = hourPrices.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(hourPrices[position])
    }

    inner class ViewHolder(private val binding: HourPriceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HourPriceViewModel.HourPriceItemModel) {
            binding.hour = Date(item.ts * 1000).toString("HH:mm")

            binding.close = item.close.significant(7, 0)

            val stonksColor = if (item.close - item.open > 0) ContextCompat.getColor(context, R.color.colorStonks)
            else ContextCompat.getColor(context, R.color.colorNotStonks)

            binding.change = (item.close - item.open).significantWithSign(6, 2)
            binding.hourPriceItemChange.setTextColor(stonksColor)
            Log.i(TAG, item.close.toString())
            Log.i(TAG, item.open.toString())
            Log.i(TAG, (item.close - item.open).toString())
            Log.i(TAG, ((item.close - item.open)/item.open*100).toString())
            Log.i(TAG,"-----------------------")
            binding.percent = ((item.close - item.open)/item.open*100).significantWithSign(6, 2)+"%"
            binding.hourPriceItemPercent.setTextColor(stonksColor)
        }

        // TODO: move somewhere else
        private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }

        // TODO: move somewhere else
        private fun Float.significantWithSign(maxLen: Int = 5, maxLast: Int = 0): String {
            return (if (this > 0) "+" else "") +
                    this.significant(if (this > 0) maxLen - 1 else maxLen, maxLast)
        }

        private fun Float.significant(maxLen: Int = 5, maxLast: Int = 0): String {
            val str = this.toString()
            if (maxLen <= 0) return str

            val p = str.indexOf('.')
            if (p >= maxLen) return str.substring(0, p) // n = 5: 12345.6 -> 12345, 1234.5 -> 1234

            var l = maxLen
            if (maxLast > 0 && p + maxLast > l) l = p + maxLast

            if (l > str.length) l = str.length
            return str.substring(0, l)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = StartFragment()

        private const val TAG = "HourPriceAdapter"
    }
}