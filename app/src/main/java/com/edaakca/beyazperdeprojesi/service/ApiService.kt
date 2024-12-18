package com.edaakca.beyazperdeprojesi.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


data class ReviewRequest(val content: String)

data class SentimentResponse(
    val result: List<SentimentResult>
)

data class SentimentResult(
    val label: String,
    val score: Double
)

interface SentimentApi {
    // POST isteği ile review nesnesi gönderilecek
    @POST("analyze")
    fun analyzeReview(@Body review: ReviewRequest): Call<SentimentResponse>
}
