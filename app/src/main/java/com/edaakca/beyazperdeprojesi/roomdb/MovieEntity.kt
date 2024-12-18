package com.edaakca.beyazperdeprojesi.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edaakca.beyazperdeprojesi.model.Actor

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val overview: String,
    val release_date: String,
    val poster_path: String?,
    val actor : String,
    val isFavorite: Boolean = false,
    var isWatched: Boolean = false // İzlenme durumu

)


@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
     val id: Int = 0,
     val movieId: Int,
     val author: String,
     val content: String
)

@Entity(tableName = "actor_table")
data class ActorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val movieId: Int,
    val name: String
)
