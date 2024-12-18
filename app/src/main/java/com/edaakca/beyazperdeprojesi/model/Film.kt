package com.edaakca.beyazperdeprojesi.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class MovieResponse(
    val results: List<Movie>
)

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val release_date: String,
    val poster_path: String,
    val actors: List<Actor>?
) : Parcelable

@Parcelize
data class CreditsResponse(
    val id: Int,
    val cast: List<Actor>,
    val crew: List<Crew>,
    val status: String? = null,
    val message: String? = null
):Parcelable

@Parcelize
data class Actor(
    val name: String,
    val character: String,
    val profile_path: String?
) : Parcelable

@Parcelize
data class Crew(
    val name: String,
    val job: String,
    val profile_path: String?
) : Parcelable


data class ReviewsResponse(
    val results: List<Review>
)

data class Review(
    val author: String,
    val content: String
)





