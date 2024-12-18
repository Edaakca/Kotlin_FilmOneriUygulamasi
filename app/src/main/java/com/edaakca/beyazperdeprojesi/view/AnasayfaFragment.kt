package com.edaakca.beyazperdeprojesi.view


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.databinding.FragmentAnasayfaBinding
import com.edaakca.beyazperdeprojesi.model.Movie
import com.edaakca.beyazperdeprojesi.model.MovieResponse
import com.edaakca.beyazperdeprojesi.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnasayfaFragment : Fragment() {
    private var _binding: FragmentAnasayfaBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchEditText: EditText // Arama çubuğu referansı


    private lateinit var movieImageView1: ImageView
    private lateinit var movieImageView2: ImageView
    private lateinit var movieImageView3: ImageView
    private lateinit var movieImageView4: ImageView
    private lateinit var movieImageView5: ImageView
    private lateinit var movieImageView6: ImageView
    private lateinit var movieImageView7: ImageView

    private lateinit var movieTitle1: TextView
    private lateinit var movieTitle2: TextView
    private lateinit var movieTitle3: TextView
    private lateinit var movieTitle4: TextView
    private lateinit var movieTitle5: TextView
    private lateinit var movieTitle6: TextView
    private lateinit var movieTitle7: TextView

    private lateinit var aksiyonImageView1: ImageView
    private lateinit var aksiyonImageView2: ImageView
    private lateinit var aksiyonImageView3: ImageView
    private lateinit var aksiyonImageView4: ImageView
    private lateinit var aksiyonImageView5: ImageView
    private lateinit var aksiyonImageView6: ImageView
    private lateinit var aksiyonImageView7: ImageView

    private lateinit var aksiyonTitle1: TextView
    private lateinit var aksiyonTitle2: TextView
    private lateinit var aksiyonTitle3: TextView
    private lateinit var aksiyonTitle4: TextView
    private lateinit var aksiyonTitle5: TextView
    private lateinit var aksiyonTitle6: TextView
    private lateinit var aksiyonTitle7: TextView

    private val apiKey = "c2b19da13b4def83efd434d1e00f7c6f"

    private lateinit var searchResultTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnasayfaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TextView bağlama
        searchResultTextView = binding.searchResultTextView // binding üzerinden erişim

        // Görseller ve başlıklar
        movieImageView1 = binding.movieImageView1
        movieImageView2 = binding.movieImageView2
        movieImageView3 = binding.movieImageView3
        movieImageView4 = binding.movieImageView4
        movieImageView5 = binding.movieImageView5
        movieImageView6 = binding.movieImageView6
        movieImageView7 = binding.movieImageView7

        movieTitle1 = binding.movieTitle1
        movieTitle2 = binding.movieTitle2
        movieTitle3 = binding.movieTitle3
        movieTitle4 = binding.movieTitle4
        movieTitle5 = binding.movieTitle5
        movieTitle6 = binding.movieTitle6
        movieTitle7 = binding.movieTitle7


        searchEditText = binding.searchEditText

        // Görseller ve başlıklar
        aksiyonImageView1 = binding.aksiyonImageView1
        aksiyonImageView2 = binding.aksiyonImageView2
        aksiyonImageView3 = binding.aksiyonImageView3
        aksiyonImageView4 = binding.aksiyonImageView4
        aksiyonImageView5 = binding.aksiyonImageView5
        aksiyonImageView6 = binding.aksiyonImageView6
        aksiyonImageView7 = binding.aksiyonImageView7

        aksiyonTitle1 = binding.aksiyonTitle1
        aksiyonTitle2 = binding.aksiyonTitle2
        aksiyonTitle3 = binding.aksiyonTitle3
        aksiyonTitle4 = binding.aksiyonTitle4
        aksiyonTitle5 = binding.aksiyonTitle5
        aksiyonTitle6 = binding.aksiyonTitle6
        aksiyonTitle7 = binding.aksiyonTitle7


        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aramaFilmleriYap(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        populerFilmleriGetir()
        enYuksekPuanliFilmGetir()

        val popularMoviesHorizontalScrollView: HorizontalScrollView = binding.popularMoviesHorizontalScrollView
        val popularMoviesLinearLayout: LinearLayout = binding.popularMoviesLinearLayout

        val actionMoviesHorizontalScrollView: HorizontalScrollView = binding.actionMoviesHorizontalScrollView
        val actionMoviesLinearLayout: LinearLayout = binding.actionMoviesLinearLayout


        popularMoviesHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            val maxScrollX = popularMoviesLinearLayout.width - popularMoviesHorizontalScrollView.width
            if (scrollX >= maxScrollX) {

                popularMoviesHorizontalScrollView.scrollTo(0, 0)
            }
        }


        actionMoviesHorizontalScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            val maxScrollX = actionMoviesLinearLayout.width - actionMoviesHorizontalScrollView.width
            if (scrollX >= maxScrollX) {

                actionMoviesHorizontalScrollView.scrollTo(0, 0)
            }
        }
    }

    private fun populerFilmleriGetir() {
        RetrofitClient.instance.getPopularMovies(apiKey, "tr-TR", 1)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful) {
                        val movies = response.body()?.results ?: emptyList()
                        if (movies.size >= 7) {
                            gorselleriYukle(movies.take(7))
                        } else {
                            Toast.makeText(context, "Yeterli film verisi bulunamadı", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Film verisi bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Toast.makeText(context, "Hata: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun enYuksekPuanliFilmGetir() {
        val language = "tr-TR"
        val page = 1
        RetrofitClient.instance.getTopRatedMovies(apiKey, language, page)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful) {
                        val movies = response.body()?.results ?: emptyList()
                        if (movies.size >= 7) {
                            gorselleriYukleAksiyon(movies.take(7))
                        } else {
                            Toast.makeText(context, "Yeterli yüksek puanlı film bulunamadı", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Yüksek puanlı film verisi bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Toast.makeText(context, "Hata: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun gorselleriYukle(movies: List<Movie>) {

        for (i in movies.indices.take(7)) {
            when (i) {
                0 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(movieImageView1)
                    movieTitle1.text = movies[i].title
                }
                1 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(movieImageView2)
                    movieTitle2.text = movies[i].title
                }
                2 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(movieImageView3)
                    movieTitle3.text = movies[i].title
                }
                3 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(movieImageView4)
                    movieTitle4.text = movies[i].title
                }
                4 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(movieImageView5)
                    movieTitle5.text = movies[i].title
                }
                5 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(movieImageView6)
                    movieTitle6.text = movies[i].title
                }
                6 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(movieImageView7)
                    movieTitle7.text = movies[i].title
                }
            }
        }
    }

    private fun gorselleriYukleAksiyon(movies: List<Movie>) {

        for (i in movies.indices.take(7)) {
            when (i) {
                0 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(aksiyonImageView1)
                    aksiyonTitle1.text = movies[i].title
                }
                1 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(aksiyonImageView2)
                    aksiyonTitle2.text = movies[i].title
                }
                2 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(aksiyonImageView3)
                    aksiyonTitle3.text = movies[i].title
                }
                3 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(aksiyonImageView4)
                    aksiyonTitle4.text = movies[i].title
                }
                4 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(aksiyonImageView5)
                    aksiyonTitle5.text = movies[i].title
                }
                5 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(aksiyonImageView6)
                    aksiyonTitle6.text = movies[i].title
                }
                6 -> {
                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500${movies[i].poster_path}")
                        .into(aksiyonImageView7)
                    aksiyonTitle7.text = movies[i].title
                }
            }
        }
    }


    private fun aramaFilmleriYap(query: String) {

            if (query.isBlank()) {
                Toast.makeText(context, "Lütfen bir anahtar kelime girin", Toast.LENGTH_SHORT).show()
                return
            }
        RetrofitClient.instance.searchMovies(apiKey, query)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful) {
                        val movies = response.body()?.results ?: emptyList()
                        if (movies.isNotEmpty()) {

                            showMovieResult(movies[0])
                        } else {
                            Toast.makeText(context, "Sonuç bulunamadı", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Hata: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Toast.makeText(context, "Hata: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showMovieResult(movie: Movie) {
        searchResultTextView.text = movie.title // Film başlığını göster
        searchResultTextView.visibility = View.VISIBLE // TextView'u görünür yap

        // Tıklama dinleyicisi ekleme
        searchResultTextView.setOnClickListener {

            val action = AnasayfaFragmentDirections.actionAnasayfaFragmentToFilmDetayFragment(movie)
            findNavController().navigate(action)

            Toast.makeText(context, "${movie.title} seçildi", Toast.LENGTH_SHORT).show()


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
