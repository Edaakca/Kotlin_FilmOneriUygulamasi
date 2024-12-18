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

interface OnMovieClickListener {
    fun onFilmClick(movie: Movie)
}

class MovieAdapter(
    private var filmler: List<Movie>,
    private val listener: OnMovieClickListener
) : RecyclerView.Adapter<MovieAdapter.FilmViewHolder>() {

    class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val baslik: TextView = view.findViewById(R.id.title)
        val aciklama: TextView = view.findViewById(R.id.overview)
        val yayinTarihi: TextView = view.findViewById(R.id.release_date)
        val poster: ImageView = view.findViewById(R.id.poster)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = filmler[position]
        holder.baslik.text = film.title
        holder.aciklama.text = film.overview
        holder.yayinTarihi.text = film.release_date


        // Poster URL'sini oluştur ve Glide ile yükle
        val posterUrl = if (film.poster_path != null) {
            "https://image.tmdb.org/t/p/w500${film.poster_path}"
        } else {
            null // Poster yoksa null döner
        }

        Glide.with(holder.itemView.context)
            .load(posterUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(holder.poster)

        // Tıklama olayını setOnClickListener ile ayarlayın
        holder.itemView.setOnClickListener {
            listener.onFilmClick(film) // Tıklandığında film bilgilerini gönder
        }
    }

    override fun getItemCount() = filmler.size

    // Filmler güncellendiğinde adapteri bilgilendir
    fun filmleriGuncelle(yeniFilmler: List<Movie>) {
        this.filmler = yeniFilmler
        notifyDataSetChanged()
    }

}
