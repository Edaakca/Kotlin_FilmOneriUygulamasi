package com.edaakca.beyazperdeprojesi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.roomdb.MovieEntity

class FavorilerAdapter(
    private val favoriFilmler: List<MovieEntity>,
    private val onRemoveFavorite: (MovieEntity) -> Unit // Kaldırma işlemi için callback
) : RecyclerView.Adapter<FavorilerAdapter.FavoriViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_movie, parent, false)
        return FavoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriViewHolder, position: Int) {
        val movie = favoriFilmler[position]
        holder.bind(movie)
    }

    override fun getItemCount() = favoriFilmler.size

    inner class FavoriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val movieTitleTextView: TextView = itemView.findViewById(R.id.movieTitleTextView)
        private val moviePosterImageView: ImageView = itemView.findViewById(R.id.moviePosterImageView)
        private val removeFavoriteButton: Button = itemView.findViewById(R.id.removeFavoriteButton) // Yeni buton

        fun bind(movie: MovieEntity) {
            movieTitleTextView.text = movie.title

            val posterUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
            Glide.with(itemView.context)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(moviePosterImageView)

            // Kaldır butonuna tıklanınca favoriden çıkar
            removeFavoriteButton.setOnClickListener {
                onRemoveFavorite(movie)
            }
        }
    }
}
