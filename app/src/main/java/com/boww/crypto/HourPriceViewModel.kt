package com.boww.crypto

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getSearch(): LiveData<MutableList<HourPriceItemModel>> = hourPrice

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun refresh(searchQuery: String) {
        viewModelScope.launch {
            try {
                val date = getCurrentDateTime()
                val dateInString = date.toString("yyyy-MM-dd")
                Log.i(TAG, "date " + dateInString)
                val resp =
                    ExchangeApi.retrofitService.getHourly(
                        from = "BTC",
                        to = "USD",
                        lim = 30,
                        ts = 1603411200,
                    )
                storeNewsItems(resp.data.data)
            } catch (e: Exception) {
                Log.i(TAG, "load failed: " + e.message)
            }
        }
    }

    private fun storeNewsItems(articles: List<ExchangeAPIResponseDataItem>) {
        val list = (hourPrice.value ?: mutableListOf())
        list.clear()

        articles.forEach {
            list.add(
                HourPriceItemModel(
                    it.time,
                    it.low,
                    it.high,
                    it.open,
                    it.close,
                ))
        }
        hourPrice.value = list
    }

    companion object {
        private const val TAG = "StartFragment"
    }
}