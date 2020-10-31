package com.boww.crypto

import android.content.Context
import android.content.SharedPreferences

class Settings(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
) : SharedPreferences.OnSharedPreferenceChangeListener {
    var fsym: String = sharedPreferences.getString(
        context.getString(R.string.from_units_key),
        context.getString(R.string.from_units_value_standard)
    )!!

    var tsym: String = sharedPreferences.getString(
        context.getString(R.string.to_units_key),
        context.getString(R.string.to_units_value_standard)
    )!!

    var market: String = sharedPreferences.getString(
        context.getString(R.string.market_key),
        context.getString(R.string.market_value_standard)
    )!!

    var days: Int = strToInt(sharedPreferences.getString(
        context.getString(R.string.num_days_key),
        DEFAULT_DAYS_STRING
    ), DEFAULT_DAYS)

    fun register() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    fun unregister() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null) {
            return
        }
        when (key) {
            context.getString(R.string.from_units_key) ->
                fsym = sharedPreferences.getString(key, context.getString(R.string.from_units_value_standard)).toString()
            context.getString(R.string.to_units_key) ->
                tsym = sharedPreferences.getString(key, context.getString(R.string.to_units_value_standard)).toString()
            context.getString(R.string.market_key) ->
                market = sharedPreferences.getString(key, context.getString(R.string.market_value_standard)).toString()
            context.getString(R.string.num_days_key) ->
                days = strToInt(sharedPreferences.getString(key, DEFAULT_DAYS_STRING), DEFAULT_DAYS)
        }
    }

    private fun strToInt(str: String?, default: Int): Int =
        if (str == null || str == "") {
            default
        } else {
            str.toInt()
        }

    companion object {
        private const val DEFAULT_DAYS = 30
        private const val DEFAULT_DAYS_STRING = "30"
    }
}