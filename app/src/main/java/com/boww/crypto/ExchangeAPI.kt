package com.boww.crypto

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class ExchangeAPIResponse(
    @Json(name = "Type") val type: Int, // 100 on success
    @Json(name = "HasWarning") val hasWarning: Boolean, // issue with no error
    @Json(name = "Data") val data: ExchangeAPIResponseData
)

data class ExchangeAPIResponseData(
//    @Json(name = "Aggregated") val aggregated: Boolean,
    @Json(name = "TimeFrom") val timeFrom: String,
    @Json(name = "TimeTo") val timeTo: String,
    @Json(name = "Data") val data: List<ExchangeAPIResponseDataItem>
)

data class ExchangeAPIResponseDataItem(
    @Json(name = "time") var time: Long,
    @Json(name = "high") var high: Float,
    @Json(name = "low") var low: Float,
    @Json(name = "open") var open: Float,
    @Json(name = "volumefrom") val volumeFrom: Float,
    @Json(name = "volumeto") val volumeTo: Float,
    @Json(name = "close") var close: Float,
    @Json(name = "conversionType") val conversionType: String,
    @Json(name = "conversionSymbol") val conversionSymbol: String,
)

object ExchangeAPI {
    private const val BASE_URL = "https://min-api.cryptocompare.com"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    interface ExchangeAPIService {
        @GET("/data/v2/histohour")
        suspend fun getHourly(
            @Query("fsym") from: String,
            @Query("tsym") to: String,
            @Query("limit") lim: Int,
            @Query("toTs") ts: Long,
            @Query("e") market: String = "CCCAGG",
        ): ExchangeAPIResponse
    }

    val retrofitService: ExchangeAPIService by lazy {
        retrofit.create(ExchangeAPIService::class.java)
    }
}

data class AggregationResult(val low: Float, val high: Float, val open: Float, val close: Float)
fun calculateAggregationMetrics(items: List<ExchangeAPIResponseDataItem>): AggregationResult {
    val open = items.first().open
    val close = items.last().close

    var high = 0f
    var low = Float.MAX_VALUE
    items.forEach {
        high = if (it.high > high) it.high else high
        low = if (it.low < low) it.low else low
    }

    return AggregationResult(low, high, open, close)
}