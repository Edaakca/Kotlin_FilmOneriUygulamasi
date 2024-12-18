package com.edaakca.beyazperdeprojesi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edaakca.beyazperdeprojesi.adapter.MovieAdapter
import com.edaakca.beyazperdeprojesi.adapter.OnMovieClickListener
import com.edaakca.beyazperdeprojesi.databinding.FragmentFilmlerBinding
import com.edaakca.beyazperdeprojesi.model.Actor
import com.edaakca.beyazperdeprojesi.model.Movie
import com.edaakca.beyazperdeprojesi.model.MovieResponse
import com.edaakca.beyazperdeprojesi.roomdb.MovieDatabase
import com.edaakca.beyazperdeprojesi.roomdb.MovieEntity
import com.edaakca.beyazperdeprojesi.service.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmlerFragment : Fragment(), OnMovieClickListener {

    private var _binding: FragmentFilmlerBinding? = null
    private val binding get() = _binding!!

    private val apiKey = "c2b19da13b4def83efd434d1e00f7c6f"
    private lateinit var movieAdapter: MovieAdapter

    private var isDataLoaded = false
    private var currentPage = 1
    private val totalPages = 100 // API'den gelen toplam sayfa bilgisi varsa burada kullanabilirsiniz.
    private var isLoading = false
    private val movies = mutableListOf<Movie>() // Yüklenen filmleri saklamak için

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmlerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        if (!isDataLoaded) {
            filmleriGoruntule()
        } else {
            veritabanindanFilmYukle()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        movieAdapter = MovieAdapter(emptyList(), this)
        binding.recyclerView.adapter = movieAdapter
        // Scroll Listener
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Eğer liste sonuna yaklaşılmışsa yeni veri yükle
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition >= totalItemCount)
                    && firstVisibleItemPosition >= 0 && currentPage < totalPages) {
                    loadMoreMovies()
                }
            }
        })
    }

    private fun loadMoreMovies() {
        isLoading = true
        loadMoviesForPage(currentPage + 1)
    }
    private fun filmleriGoruntule() {
        loadMoviesForPage(currentPage)
    }


    private fun loadMoviesForPage(page: Int) {
        binding.progressBar.visibility = View.VISIBLE // Yükleme göstergesi

        RetrofitClient.instance.getPopularMovies(apiKey, "tr-TR", page)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        response.body()?.results?.let { newMovies ->
                            movies.addAll(newMovies)
                            movieAdapter.filmleriGuncelle(movies)
                            currentPage = page
                            isLoading = false
                            // İlk sayfa ise veritabanına kaydedin
                            if (page == 1) veritabaninaKayitEt(movies)
                        }
                    } else {
                        isLoading = false
                        showErrorMessage("Filmler yüklenemedi.")
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    isLoading = false
                    showErrorMessage("Bağlantı hatası: ${t.message}")
                }
            })
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onFilmClick(movie: Movie) {
        val action = FilmlerFragmentDirections.actionFilmlerFragmentToDetayFragment(movie)
        findNavController().navigate(action)
    }
    private fun veritabaninaKayitEt(movies: List<Movie>) {

        if (view != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val movieEntities = movies.map { movie ->
                    MovieEntity(
                        title = movie.title,
                        overview = movie.overview,
                        release_date = movie.release_date,
                        poster_path = movie.poster_path ?: "",
                        actor = movie.actors?.joinToString(", ") { it.name } ?: ""
                    )
                }
                val database = MovieDatabase.getDatabase(requireContext())
                database.movieDao().insertMovies(movieEntities)
            }
        }
    }

    private fun veritabanindanFilmYukle() {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())
            val movies = database.movieDao().getAllMovies()
            movieAdapter.filmleriGuncelle(movies.map { movieEntity ->
                Movie(
                    id = movieEntity.id,
                    title = movieEntity.title,
                    overview = movieEntity.overview,
                    release_date = movieEntity.release_date,
                    poster_path = movieEntity.poster_path ?: "",
                    actors = movieEntity.actor.split(", ").map { actorName ->
                        Actor(name = actorName, character = "", profile_path = null)
                    }
                )
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
