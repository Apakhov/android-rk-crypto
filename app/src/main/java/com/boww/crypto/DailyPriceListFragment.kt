package com.boww.crypto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boww.crypto.databinding.FragmentDailyPriceListBinding


class DailyPriceListFragment : Fragment() {
    private lateinit var binding: FragmentDailyPriceListBinding
    private lateinit var settings: Settings

    private val model: DailyPriceViewModel by viewModels()
    private val dailyPriceItems = mutableListOf<DailyPriceViewModel.DailyPriceItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = Settings(requireContext(),  PreferenceManager.getDefaultSharedPreferences(context))
        settings.register()

        Log.i(TAG, "Fragment created. Settings connected")
    }

    override fun onDestroy() {
        settings.unregister()

        Log.i(TAG, "Settings disconnected")
        super.onDestroy()
    }

    fun refresh() {
        model.refresh(settings.fsym, settings.tsym, settings.market, settings.days-1)
        Log.i(TAG, "Refreshed")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_price_list, container, false)

        val dailyPriceListAdapter = DailyPriceAdapter(requireContext(), dailyPriceItems)
        val dailyPriceListManager = LinearLayoutManager(requireContext())

        binding.dailyPriceList.apply {
            adapter = dailyPriceListAdapter
            layoutManager = dailyPriceListManager
        }

        model.getDailyPriceList().observe(viewLifecycleOwner, { listSnapshot ->
            dailyPriceItems.clear()
            dailyPriceItems.addAll(listSnapshot)

            dailyPriceListAdapter.notifyDataSetChanged()
        })

        model.getCurrentPrice().observe(viewLifecycleOwner, {newValue ->
            binding.currentPrice = newValue.significant(9, 2)
        })

        val exchangeSpan = buildSpanExchangeBar(
            settings.fsym, ContextCompat.getColor(requireContext(), R.color.colorMainPink),
            settings.tsym, ContextCompat.getColor(requireContext(), R.color.colorMainWhite),
            DELIMITER, ContextCompat.getColor(requireContext(), R.color.colorMainBlack),
        )
        binding.exchangeString.setText(exchangeSpan, TextView.BufferType.SPANNABLE)

        binding.market = settings.market

        refresh()
        Log.i(TAG, "View created")
        return binding.root
    }

    companion object {
        private const val TAG = "DailyPriceListFragment"
        private const val DELIMITER = "/"

        @JvmStatic
        fun newInstance() = DailyPriceListFragment()
    }
}