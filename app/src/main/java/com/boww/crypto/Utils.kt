package com.boww.crypto

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

fun Date.ts(): Long {
    return this.time / 1000
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

fun isToday(date: Date): Boolean {
    return (beginOfDay(getCurrentDate()).time == beginOfDay(date).time)
}

fun getCurrentDate(): Date {
    return Calendar.getInstance().time
}

fun Long.toDate(): Date {
    return Date(this * 1000)
}

fun buildSpanExchangeBar(
    fSym: String, fColor: Int,
    tSym: String, tColor: Int,
    sepSym: String, sepColor: Int
)
        : SpannableStringBuilder{
    val builder = SpannableStringBuilder()

    val str1 = SpannableString(fSym)
    str1.setSpan(ForegroundColorSpan(fColor), 0, str1.length, 0)
    builder.append(str1)

    val str2 = SpannableString(sepSym)
    str2.setSpan(ForegroundColorSpan(sepColor), 0, str2.length, 0)
    builder.append(str2)

    val str3 = SpannableString(tSym)
    str3.setSpan(ForegroundColorSpan(tColor), 0, str3.length, 0)
    builder.append(str3)

    return builder
}

fun beginOfHour(date: Date): Date {
    val calendar = date.toCalendar()

    calendar.set(
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), 0, 0,
    )
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun endOfHour(date: Date): Date {
    val calendar = date.toCalendar()

    calendar.set(
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), 59, 59
    )
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}

fun hoursBetween(date1: Date, date2: Date): Long {
    return abs(date1.time - date2.time) / (60 * 60 * 1000)
}

fun beginOfDay(date: Date): Date {
    val calendar = date.toCalendar()

    calendar.set(
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DATE), 0, 0, 0
    )
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun endOfDay(date: Date): Date {
    val calendar = date.toCalendar()

    calendar.set(
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DATE), 23, 59, 59
    )
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}