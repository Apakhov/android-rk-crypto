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

class HourPriceViewModel : ViewModel() {
    data class HourPriceItemModel(
        val ts: Long,
        val low: Float,
        val high: Float,
        val open: Float,
        val close: Float,
    )

    private val hourPrice: MutableLiveData<MutableList<HourPriceItemModel>> = MutableLiveData()
    var lastHourPrice: List<ExchangeAPIResponseDataItem> = emptyList()


    fun getSearch(): LiveData<MutableList<HourPriceItemModel>> = hourPrice

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun refresh(
        fsym: String,
        tsym: String,
        market: String,
        limit: Int,
        ts: Long,
    ): Job {
        Log.i("DATE_TAG4", ts.toString())
        return viewModelScope.launch {
            try {
                var res =
                    ExchangeApi.retrofitService.getHourly(
                        from = fsym,
                        to = tsym,
                        lim = if (isToday(ts)) hoursSinceDayStarted(ts) + 1 else 25,
                        ts = if (isToday(ts)) ts else dayEnd(ts),
                    ).data.data

                // api gives time on start of period so we need to correct time to show time on period end
                // move all forward 3600, move last forward minutesSinceBeginOfHour
                res.forEach {
                    it.time += 3600
                }
                val minutes = getCurrentDateTime().toString("mm").toInt()
                if (hoursSinceDayStarted(ts) < 24 && minutes > 0) {
                    res.last().time += 60 * minutes - 3600
                } else {
                    res = res.subList(0, res.size - 1)
                }

                storeInfoItems(res.reversed())
            } catch (e: Exception) {
                Log.i(TAG, "load failed: " + e.message)
            }
        }
    }

    private fun storeInfoItems(items: List<ExchangeAPIResponseDataItem>) {
        val list = (hourPrice.value ?: mutableListOf())
        list.clear()

        items.forEach {
            list.add(
                HourPriceItemModel(
                    it.time,
                    it.low,
                    it.high,
                    it.open,
                    it.close,
                )
            )
        }
        lastHourPrice = items
        Log.i(TAG, "has values")
        hourPrice.value = list
    }

    companion object {
        private const val TAG = "StartFragment"
    }
}