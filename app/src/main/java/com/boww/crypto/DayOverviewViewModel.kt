package com.boww.crypto

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.minutes
import kotlin.time.seconds

class DayOverviewViewModel : ViewModel() {
    data class HourPriceItem(
        val ts: Long,
        val low: Float,
        val high: Float,
        val open: Float,
        val close: Float,
    )

    data class DayPriceInfo(
        val ts: Long,
        val low: Float,
        val high: Float,
        val open: Float,
        val close: Float,
    )

    private val hourPriceList: MutableLiveData<MutableList<HourPriceItem>> = MutableLiveData()
    private val dayInfo: MutableLiveData<DayPriceInfo> = MutableLiveData()

    fun getHourPriceList(): LiveData<MutableList<HourPriceItem>> = hourPriceList
    fun getDayInfo(): LiveData<DayPriceInfo> = dayInfo

    private fun storeHourPriceItems(items: List<ExchangeAPIResponseDataItem>) {
        val list = (hourPriceList.value ?: mutableListOf())
        list.clear()

        items.forEach {
            list.add(HourPriceItem(it.time, it.low, it.high, it.open, it.close))
        }

        hourPriceList.value = list
    }

    private fun storeDayInfo(items: List<ExchangeAPIResponseDataItem>) {
        var open = items.first().open
        var close = items.first().close

        var high = 0f
        var low = Float.MAX_VALUE
        items.forEach {
            high = if (it.high > high) it.high else high
            low = if (it.low < low) it.low else low
        }

        dayInfo.value = DayPriceInfo(items.first().time, low, high, open, close)
    }

    @ExperimentalTime
    fun refresh(fsym: String, tsym: String, market: String, limit: Int, ts: Long): Job {
        return viewModelScope.launch {
            try {
                Log.i(TAG, "Refreshing with: fsym=$fsym, tsym=$tsym, limit=$limit, ts=$ts, market=$market")
                val resp = ExchangeAPI.retrofitService.getHourly(fsym, tsym, limit, ts, market)
                var priceData = resp.data.data

                // api gives time on start of period so we need to correct time to show time on period end
                // move all forward 1 hour, move last forward minutesSinceBeginOfHour
                priceData.forEach {
                    it.time += 1.hours.inSeconds.toLong()
                }

                val min = getCurrentDate().toCalendar().get(Calendar.MINUTE)
                if (isToday(ts.toDate()) && min > 0) {
                    priceData.last().time += min.minutes.inSeconds.toLong() - 1.hours.inSeconds.toLong()
                }

                storeDayInfo(priceData)
                storeHourPriceItems(priceData.reversed())
                Log.i(TAG, "Hour price data loaded")
            } catch (e: Exception) {
                Log.i(TAG, "Hour price load failed: " + e.message)
            }
        }
    }

    companion object {
        private const val TAG = "DayOverviewViewModel"
    }
}