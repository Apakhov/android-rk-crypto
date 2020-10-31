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


    private fun storeDailyPriceItems(a: List<ExchangeAPIResponseDataItem>) {
        var priceList = dailyPriceList.value
        priceList = priceList ?: mutableListOf()
        priceList.clear()

        a.forEach {
            priceList.add(DailyPriceItem(it.time, it.open, it.close))
        }

        dailyPriceList.value = priceList
    }

    private fun storeCurrentPrice(p: Float) {
        currentPrice.value = p
    }

    fun refresh(fsym: String, tsym: String, market: String, limit: Int) {
        viewModelScope.launch {
            try {
                Log.i(TAG, "Refreshing with: fsym=$fsym, tsym=$tsym, limit=$limit, market=$market")
                val resp = ExchangeAPI.retrofitService.getDaily(fsym, tsym, limit, market)
                val priceData = resp.data.data.reversed()

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
    }
}