package com.edaakca.beyazperdeprojesi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.model.Movie

class AramaMovieAdapter(
    private var movies: List<Movie>,
    private val listener: OnMovieClickListener
) : RecyclerView.Adapter<AramaMovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val posterImageView: ImageView = view.findViewById(R.id.aramaMoviePosterImageView)
        val titleTextView: TextView = view.findViewById(R.id.aramaMovieTitleTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.aramaMovieDateTextView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_arama_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleTextView.text = movie.title

        Glide.with(holder.itemView.context)
            .load("https://image.tmdb.org/t/p/w200${movie.poster_path}")
            .into(holder.posterImageView)

        holder.itemView.setOnClickListener {
            listener.onFilmClick(movie)
        }
    }

    override fun getItemCount(): Int = movies.size

    fun filmleriGuncelle(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}
