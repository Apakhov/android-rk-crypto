package com.boww.crypto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boww.crypto.databinding.FragmentDayOverviewBinding
import java.util.*
import kotlin.time.ExperimentalTime

class DayOverviewFragment : Fragment() {
    private lateinit var binding: FragmentDayOverviewBinding
    private lateinit var settings: Settings

    private val model: DayOverviewViewModel by viewModels()
    private val hourPriceItems = mutableListOf<DayOverviewViewModel.HourPriceItem>()

    // based on navigation arguments
    private var limit: Int = 0
    private var ts: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = Settings(requireContext(),  PreferenceManager.getDefaultSharedPreferences(context))
        settings.register()

        arguments?.let {
            val beginOfPeriod = it.getLong(ARG_BEGIN_OF_DAY).toDate()
            val endOfPeriod = minOf(beginOfHour(getCurrentDate()), beginOfHour(endOfDay(beginOfPeriod)))

            limit = hoursBetween(endOfPeriod, beginOfPeriod).toInt() + 1 // add 1 to include bounds
            ts = endOfPeriod.ts()
        }

        Log.i(TAG, "Fragment created. Settings connected")
    }

    override fun onDestroy() {
        settings.unregister()

        Log.i(TAG, "Settings disconnected")
        super.onDestroy()
    }

    @ExperimentalTime
    fun refresh() {
        model.refresh(settings.fsym, settings.tsym, settings.market, limit, ts)
        Log.i(TAG, "Refreshed")
    }

    @ExperimentalTime
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_overview, container, false)

        val hourPriceListAdapter = DayOverviewAdapter(requireContext(), hourPriceItems)
        val hourPriceListManager = LinearLayoutManager(requireContext())

        binding.hourPriceItemList.apply {
            setHasFixedSize(true)
            adapter = hourPriceListAdapter
            layoutManager = hourPriceListManager
        }

        model.getHourPriceList().observe(viewLifecycleOwner, { listSnapshot ->
            hourPriceItems.clear()
            hourPriceItems.addAll(listSnapshot)

            hourPriceListAdapter.notifyDataSetChanged()
        })
        
        model.getDayInfo().observe(viewLifecycleOwner, { dayPriceInfo ->
            binding.date = dayPriceInfo.ts.toDate().toString("dd MMM yyyy").toUpperCase(Locale.getDefault())

            binding.high = "${dayPriceInfo.high.significant(6, 2)} ${settings.tsym}"
            binding.low = "${dayPriceInfo.low.significant(6, 2)} ${settings.tsym}"
            binding.open = "${dayPriceInfo.open.significant(6, 2)} ${settings.tsym}"
            binding.close = "${dayPriceInfo.close.significant(6, 2)} ${settings.tsym}"
            binding.change = "${(dayPriceInfo.close - dayPriceInfo.open).significantWithSign(6, 2)} ${settings.tsym}"

            val stonksColor =
                if (dayPriceInfo.close - dayPriceInfo.open > 0) ContextCompat.getColor(requireContext(), R.color.colorOnBlueStonks)
                else ContextCompat.getColor(requireContext(), R.color.colorOnBlueNotStonks)
            binding.changeValue.setTextColor(stonksColor)
        })

        val exchangeSpan = buildSpanExchangeBar(
            settings.fsym, ContextCompat.getColor(requireContext(), R.color.colorMainPink),
            settings.tsym, ContextCompat.getColor(requireContext(), R.color.colorMainWhite),
            DELIMITER, ContextCompat.getColor(requireContext(), R.color.colorMainBlack),
        )
        binding.exchangeString.setText(exchangeSpan, TextView.BufferType.SPANNABLE)

        refresh()
        Log.i(TAG, "View created")
        return binding.root
    }

    companion object {
        const val ARG_BEGIN_OF_DAY = "begin_of_day"

        private const val TAG = "DayOverviewFragment"
        private const val DELIMITER = "/"

        fun newInstance(beginOfDay: Long) =
            DayOverviewFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_BEGIN_OF_DAY, beginOfDay)
                }
            }
    }
}