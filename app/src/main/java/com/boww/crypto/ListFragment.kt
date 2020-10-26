package com.boww.crypto

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
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
import com.boww.crypto.databinding.FragmentListBinding
import kotlinx.coroutines.runBlocking

private const val ARG_HIGH_SYM = "high"
private const val ARG_LOW_SYM = "low"
private const val ARG_OPEN_SYM = "open"
private const val ARG_CLOSE_SYM = "close"
private const val ARG_FROM_SYM = "fsym"
private const val ARG_TO_SYM = "tsym"
private const val ARG_MARKET = "market"
private const val ARG_LIMIT = "limit"
private const val ARG_TIMESTAMP = "timestamp"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private var fsym: String = "unused"
    private var tsym: String = "unused"
    private var market: String = "CCCAGG"
    private var limit: Int = 24 // Every hour of day
    private var ts: Long = 1603567235 // random modern date
    private var high: Float = 0f
    private var low: Float = 0f
    private var open: Float = 0f
    private var close: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        arguments?.let {
            market = it.getString(ARG_MARKET, market)
            limit = it.getInt(ARG_LIMIT, limit)
            ts = it.getLong(ARG_TIMESTAMP, ts)
            high = it.getFloat(ARG_HIGH_SYM, high)
            low = it.getFloat(ARG_LOW_SYM, low)
            open = it.getFloat(ARG_OPEN_SYM, open)
            close = it.getFloat(ARG_CLOSE_SYM, close)
        }
    }

    private lateinit var binding: FragmentListBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val model: HourPriceViewModel by viewModels()
    private val searchItems = mutableListOf<HourPriceViewModel.HourPriceItemModel>()

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null) {
            return
        }
        when (key) {
            getString(R.string.from_units_key) ->
                fsym = key
            getString(R.string.to_units_key) ->
                tsym = key
        }
//        refresh()
    }

    fun refresh() {
        Log.i("DATE_TAG3", ts.toString())
        model.refresh(
            fsym,
            tsym,
            market,
            limit,
            ts,
        ).invokeOnCompletion { throwable ->
            if (throwable == null) {
                high = 0f
                low = Float.MAX_VALUE
                model.lastHourPrice.forEach {
                    high = if (it.high > high) it.high else high
                    low = if (it.low < low) it.low else low
                }
                close = model.lastHourPrice.first().close
                open = model.lastHourPrice.last().close
            }
            Log.i(TAG, "called bind")
            bind()
        }
    }

    override fun onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onDestroy()
    }

    fun bind() {
        val oldTsym = tsym
        val oldFsym = fsym
        fsym = sharedPreferences.getString(
            getString(R.string.from_units_key),
            getString(R.string.from_units_value_standard)
        )!!
        tsym = sharedPreferences.getString(
            getString(R.string.to_units_key),
            getString(R.string.to_units_value_standard)
        )!!

        Log.i("DATE_TAG2", ts.toString())
        Log.i(TAG, oldTsym + " " + tsym.toString() + " call refr " + ((oldFsym != fsym) || (oldTsym != tsym)).toString())
        if ((oldFsym != fsym) || (oldTsym != tsym)) {
            refresh()
            return
        }




        makeSpanExchangeBar(fsym, tsym)
        binding.dateString.text = ts.toDate().toString("dd MMM yyyy").toUpperCase()
        binding.highValue.text = high.significant(6, 2)+" "+tsym // мне лень фиксить
        binding.lowValue.text = low.significant(6, 2)+" "+tsym // мне лень фиксить
        binding.openValue.text = open.significant(6, 2)+" "+tsym // мне лень фиксить
        binding.closeValue.text = close.significant(6, 2)+" "+tsym // мне лень фиксить
        binding.changeValue.text = (close - open).significantWithSign(6, 2)+" "+tsym // мне лень фиксить
        val stonksColor = if (close - open > 0) ContextCompat.getColor(
            requireContext(),
            R.color.colorOnBlueStonks
        )

        else ContextCompat.getColor(requireContext(), R.color.colorOnBlueNotStonks)
        binding.changeValue.setTextColor(stonksColor)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)

        val searchListAdapter = HourPriceAdapter(requireContext(), searchItems)
        val searchListManager = LinearLayoutManager(requireContext())

        binding.hourPriceItemList.apply {
            setHasFixedSize(true)
            adapter = searchListAdapter
            layoutManager = searchListManager
        }

        model.getSearch().observe(viewLifecycleOwner) { listSnapshot ->
            Log.i(TAG, "New search items from view model")
            searchItems.clear()
            searchItems.addAll(listSnapshot)
            searchListAdapter.notifyDataSetChanged()
        }

        Log.i(TAG, "fsym $fsym")
        Log.i(TAG, "tsym $tsym")
        Log.i(TAG, "market $market")
        Log.i(TAG, "limit $limit")
        Log.i(TAG, "ts $ts")


        bind()
        model.refresh(
            fsym,
            tsym,
            market,
            limit,
            ts,
        )



        return binding.root
    }

    fun makeSpanExchangeBar(fsym: String, tsym: String) {
        val builder = SpannableStringBuilder()

        val str1 = SpannableString(fsym)
        str1.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorMainPink
                )
            ), 0, str1.length, 0
        )
        builder.append(str1)

        val str2 = SpannableString("/")
        str2.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorMainBlack
                )
            ), 0, str2.length, 0
        )
        builder.append(str2)

        val str3 = SpannableString(tsym)
        str3.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorMainWhite
                )
            ), 0, str3.length, 0
        )
        builder.append(str3)

        binding.exchangeString.setText(builder, TextView.BufferType.SPANNABLE)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param requestString .
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            fsym: String,
            tsym: String,
            market: String,
            limit: Int,
            ts: Long,
            high: Float,
            low: Float,
            open: Float,
            close: Float,
        ) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FROM_SYM, fsym)
                    putString(ARG_TO_SYM, tsym)
                    putString(ARG_MARKET, market)
                    putInt(ARG_LIMIT, limit)
                    putLong(ARG_TIMESTAMP, ts)
                    putFloat(ARG_HIGH_SYM, high)
                    putFloat(ARG_LOW_SYM, low)
                    putFloat(ARG_OPEN_SYM, open)
                    putFloat(ARG_CLOSE_SYM, close)
                }
            }

        private const val TAG = "ListFragment"
    }
}