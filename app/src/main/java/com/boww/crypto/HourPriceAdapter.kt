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
//            Log.i(TAG, item.toString())
//            Log.i(TAG, item.close.toString())
//            Log.i(TAG, item.open.toString())
//            Log.i(TAG, (item.close - item.open).toString())
//            Log.i(TAG, ((item.close - item.open)/item.open*100).toString())
//            Log.i(TAG,"-----------------------")
            binding.percent = ((item.close - item.open)/item.open*100).significantWithSign(6, 2)+"%"
            binding.hourPriceItemPercent.setTextColor(stonksColor)
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