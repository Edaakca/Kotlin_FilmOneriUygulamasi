package com.edaakca.beyazperdeprojesi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.roomdb.MovieEntity

class IzlemeListAdapter(
    private val movieList: List<MovieEntity>,
    private val onCheckChanged: (MovieEntity, Boolean) -> Unit // CheckBox işaretleme işlemi için callback
) : RecyclerView.Adapter<IzlemeListAdapter.IzlemeListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IzlemeListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_watchlist, parent, false)
        return IzlemeListViewHolder(view)
    }

    override fun onBindViewHolder(holder: IzlemeListViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie)
    }

    override fun getItemCount() = movieList.size

    inner class IzlemeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val movieTitleTextView: TextView = itemView.findViewById(R.id.movieTitleTextView)
        private val moviePosterImageView: ImageView = itemView.findViewById(R.id.moviePosterImageView)
        private val watchCheckBox: CheckBox = itemView.findViewById(R.id.watchCheckBox)

        fun bind(movie: MovieEntity) {
            movieTitleTextView.text = movie.title

            val posterUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
            Glide.with(itemView.context)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(moviePosterImageView)

            // CheckBox'ın durumu, filme ait izleme bilgisine göre ayarlanacak
            watchCheckBox.isChecked = movie.isWatched // isWatched, MovieEntity'deki izlenip izlenmediğini belirten bir alan olmalı

            // CheckBox durumu değiştiğinde geri çağırma yapılacak
            watchCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckChanged(movie, isChecked)
            }
        }
    }
}
