package com.edaakca.beyazperdeprojesi.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SentimentApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val instance: SentimentApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SentimentApi::class.java)
    }
}
