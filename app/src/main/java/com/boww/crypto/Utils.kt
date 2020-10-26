package com.boww.crypto

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun Float.significantWithSign(maxLen: Int = 5, maxLast: Int = 0): String {
    return (if (this > 0) "+" else "") +
            this.significant(if (this > 0) maxLen - 1 else maxLen, maxLast)
}

fun Float.significant(maxLen: Int = 5, maxLast: Int = 0): String {
    val str = this.toString()
    if (maxLen <= 0) return str

    val p = str.indexOf('.')
    if (p >= maxLen) return str.substring(0, p) // n = 5: 12345.6 -> 12345, 1234.5 -> 1234

    var l = maxLen
    if (maxLast > 0 && p + maxLast > l) l = p + maxLast

    if (l > str.length) l = str.length
    if (str[l - 1] == '.') l -= 1
    return str.substring(0, l)
}

fun dayEnd(ts: Long, locale: Locale = Locale.getDefault()): Long {
    //current + (sec in day - sec passed since day begin)
    val dayInZone = ts + (24 * 3600 - (ts % (24 * 3600)))
    val utcOffH = dayInZone.toDate().toString("HH", Locale.UK).toInt()
    val utcOffM = dayInZone.toDate().toString("mm", Locale.UK).toInt()
    val utcOff =
        if (utcOffH * 60 * utcOffM < 12 * 60) -(utcOffH * 60 - utcOffM) * 60 else 24 * 3600 - (utcOffH * 60 - utcOffM) * 60

    Log.i("DATA_TAG day_end ",dayInZone.toString()+" "+utcOffH+" "+ utcOffM+" "+utcOff)
    return dayInZone + utcOff
}

fun hoursSinceDayStarted(ts: Long, locale: Locale = Locale.getDefault()): Int {
    val hours = ts.toDate().toString("HH", locale).toInt()
    val add = if (isToday(ts, locale)) 0 else 24
    Log.i("DATE_TAG hours_sice",ts.toString()+" "+ hours.toString() + " " + add.toString())
    return hours + add
}

fun isToday(ts: Long, locale: Locale = Locale.getDefault()): Boolean {
    Log.i("DATE_TAG isToday", (getCurrentDateTime().toString("yyyy-MM-dd", locale) ==
            ts.toDate().toString("yyyy-MM-dd", locale)).toString())
    Log.i("TAG is today",getCurrentDateTime().toString("yyyy-MM-dd", locale)+" "+ts.toDate().toString("yyyy-MM-dd", locale))
    return (getCurrentDateTime().toString("yyyy-MM-dd", locale) ==
            ts.toDate().toString("yyyy-MM-dd", locale))
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun Date.ts(): Long {
    return this.time / 1000
}

fun Long.toDate(): Date {
    return Date(this * 1000)
}