package com.edaakca.beyazperdeprojesi.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edaakca.beyazperdeprojesi.model.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: MovieEntity)

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    suspend fun getAllFavoriteMovies(): List<MovieEntity> // MovieEntity olarak döndürmelisiniz

    @Delete
    suspend fun deleteMovie(movie: MovieEntity) // Film çıkarma metodu


    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("SELECT * FROM reviews WHERE movieId = :movieId")
    suspend fun getReviewsByMovieId(movieId: Int): List<ReviewEntity>

    @Insert
    suspend fun insertActors(actors: List<ActorEntity>)

    @Query("SELECT * FROM actor_table WHERE movieId = :movieId")
    suspend fun getActorsByMovieId(movieId: Int): List<ActorEntity>



}