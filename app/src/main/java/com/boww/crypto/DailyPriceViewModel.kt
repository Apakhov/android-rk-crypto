package com.boww.crypto

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

class DailyPriceViewModel : ViewModel() {
    private val dailyPriceList: MutableLiveData<MutableList<DailyPriceItem>> = MutableLiveData()
    private val currentPrice:  MutableLiveData<Float> = MutableLiveData()

    data class DailyPriceItem(
        val ts: Long,
        val open: Float,
        val close: Float,
    )

    fun getDailyPriceList(): LiveData<MutableList<DailyPriceItem>> = dailyPriceList
    fun getCurrentPrice(): LiveData<Float> = currentPrice

    private fun storeDailyPriceItems(a: List<DailyPriceItem>) {
        var priceList = dailyPriceList.value
        priceList = priceList ?: mutableListOf()
        priceList.clear()
        priceList.addAll(a)
        dailyPriceList.value = priceList
    }

    private fun storeCurrentPrice(p: Float) {
        currentPrice.value = p
    }

    fun refresh(fsym: String, tsym: String, market: String, days: Int) {
        viewModelScope.launch {
            try {
                val now = beginOfHour(getCurrentDate())
                val beginOfToday = beginOfDay(now)
                // (days - 1) * 24 means full days without today
                // hoursBetween(now, beginOfToday) hours count of current day, including bounds
                val limit = ((days - 1) * DAY_HOURS + hoursBetween(now, beginOfToday)).toInt()
                val ts = now.ts()

                Log.i(TAG, "Refreshing with: fsym=$fsym, tsym=$tsym, limit=$limit, ts=$ts, market=$market")
                val resp = ExchangeAPI.retrofitService.getHourly(fsym, tsym, limit, ts, market)

                val priceDataAggregated = resp.data.data.chunked(DAY_HOURS) {
                    val (_, _, open, close) = calculateAggregationMetrics(it)
                    return@chunked DailyPriceItem(it[0].time, open, close)
                }
                val priceData = priceDataAggregated.reversed()

                storeDailyPriceItems(priceData)
                storeCurrentPrice(priceData[0].close) // API always returns 2 elements at least

                Log.i(TAG, "Daily price data loaded")
            } catch (e: Exception) {
                Log.i(TAG, "Load daily price failed: " + e.message)
            }
        }
    }

    companion object {
        private const val TAG = "DailyPriceViewModel"
        private const val DAY_HOURS = 24
    }
}