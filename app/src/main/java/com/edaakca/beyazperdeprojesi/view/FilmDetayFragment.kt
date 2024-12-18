package com.edaakca.beyazperdeprojesi.view

import com.edaakca.beyazperdeprojesi.model.Movie
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.databinding.FragmentFilmDetayBinding
import com.edaakca.beyazperdeprojesi.model.Actor
import com.edaakca.beyazperdeprojesi.model.CreditsResponse
import com.edaakca.beyazperdeprojesi.model.ReviewsResponse
import com.edaakca.beyazperdeprojesi.roomdb.MovieDatabase
import com.edaakca.beyazperdeprojesi.roomdb.MovieEntity
import com.edaakca.beyazperdeprojesi.roomdb.ReviewEntity
import com.edaakca.beyazperdeprojesi.service.RetrofitClient
import com.edaakca.beyazperdeprojesi.model.Review
import com.edaakca.beyazperdeprojesi.roomdb.ActorEntity
import com.edaakca.beyazperdeprojesi.service.ReviewRequest
import com.edaakca.beyazperdeprojesi.service.SentimentApiClient
import com.edaakca.beyazperdeprojesi.service.SentimentResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmDetayFragment : Fragment() {

    private var _binding: FragmentFilmDetayBinding? = null
    private val binding get() = _binding!!
    private val args: FilmDetayFragmentArgs by navArgs()
    private lateinit var movie: Movie
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmDetayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movie = args.movie

        favoriKontrolEt(movie.id)

        binding.favoriteButton.setOnClickListener {
            favoriDegisimi(movie)
        }


        binding.titleTextView.text = movie.title
        binding.overviewTextView.text = movie.overview
        binding.releaseDateTextView.text = movie.release_date

        // Poster yükle
        val posterUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
        Glide.with(this)
            .load(posterUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(binding.posterImageView)


        binding.actorsTextView.visibility = View.VISIBLE

        oyuncuIsimAl(movie.id)

        yorumlariAl(movie.id)
    }


    private fun favoriDegisimi(movie: Movie) {
        if (kullaniciGirisKontrol()) {
            viewLifecycleOwner.lifecycleScope.launch {
                val database = MovieDatabase.getDatabase(requireContext())
                val existingMovie = database.movieDao().getMovieById(movie.id)

                if (existingMovie != null) {
                    // Film favorilerde, çıkarma işlemi yap
                    database.movieDao().deleteMovie(existingMovie)
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite_border) // Beyaz simge
                    Toast.makeText(requireContext(), "${movie.title} favorilerden çıkarıldı", Toast.LENGTH_SHORT).show()
                    isFavorite = false
                } else {
                    // Film favorilerde değil, ekleme işlemi yap
                    val movieEntity = MovieEntity(
                        id = movie.id,
                        title = movie.title,
                        overview = movie.overview,
                        release_date = movie.release_date,
                        poster_path = movie.poster_path,
                        actor = "",
                        isFavorite = true
                    )
                    database.movieDao().insertMovies(movieEntity)
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite_red) // Kırmızı simge
                    Toast.makeText(requireContext(), "${movie.title} favorilere eklendi", Toast.LENGTH_SHORT).show()
                    isFavorite = true
                }
            }
        } else {

            Toast.makeText(requireContext(), "Favori eklemek için giriş yapmalısınız.", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun kullaniciGirisKontrol(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    private fun favoriKontrolEt(movieId: Int) {
        if (kullaniciGirisKontrol()) {
            // Kullanıcı giriş yapmışsa, favori kontrolünü yap
            viewLifecycleOwner.lifecycleScope.launch {
                val database = MovieDatabase.getDatabase(requireContext())
                val existingMovie = database.movieDao().getMovieById(movieId)

                if (existingMovie != null) {
                    isFavorite = true
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite_red) // Kırmızı simge
                } else {
                    isFavorite = false
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite_border) // Beyaz simge
                }
            }
        } else {
            binding.favoriteButton.setOnClickListener {
                Toast.makeText(requireContext(), "Favori eklemek için giriş yapmalısınız.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun oyuncuIsimAl(movieId: Int) {
        val apiKey = "c2b19da13b4def83efd434d1e00f7c6f"
        RetrofitClient.instance.getMovieCredits(movieId, apiKey).enqueue(object : Callback<CreditsResponse> {
            override fun onResponse(call: Call<CreditsResponse>, response: Response<CreditsResponse>) {
                if (isAdded && view != null) { // Görünümün mevcut olup olmadığını kontrol et
                    if (response.isSuccessful) {
                        val creditsResponse = response.body()
                        creditsResponse?.let {
                            val actorNames = it.cast.joinToString(", ") { actor -> actor.name }
                            binding.actorsTextView.text = actorNames

                            aktorleriVeritabaninaKayitEt(movieId, it.cast)

                        }
                    } else {
                        Toast.makeText(requireContext(), "Hata: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FilmDetayFragment", "Görünüm mevcut değil, API yanıtı işlenemiyor.")

                    aktorleriVeritabanindanYukle(movieId)
                }
            }

            override fun onFailure(call: Call<CreditsResponse>, t: Throwable) {
                if (isAdded && view != null) { // Görünümün mevcut olup olmadığını kontrol edin
                    Toast.makeText(requireContext(), "API çağrısı başarısız: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun aktorleriVeritabaninaKayitEt(movieId: Int, actors: List<Actor>) {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())

            // Veritabanında zaten var mı diye kontrol et
            val existingActors = database.movieDao().getActorsByMovieId(movieId)
            if (existingActors.isEmpty()) {
                // Eğer oyuncular veritabanında yoksa, yeni oyuncuları ekle
                val actorEntities = actors.map { actor ->
                    ActorEntity(
                        movieId = movieId,
                        name = actor.name
                    )
                }
                database.movieDao().insertActors(actorEntities)
            }
        }
    }
    private fun yorumlariAl(movieId: Int) {
        val apiKey = "c2b19da13b4def83efd434d1e00f7c6f"
        val language = "tr"

        RetrofitClient.instance.getMovieReviews(movieId, apiKey, language).enqueue(object : Callback<ReviewsResponse> {
            override fun onResponse(call: Call<ReviewsResponse>, response: Response<ReviewsResponse>) {
                if (response.isSuccessful) {
                    val reviewsResponse = response.body()
                    reviewsResponse?.let {
                        val reviewsText = it.results.joinToString("\n\n") { review ->
                            "${review.author}: ${review.content}"
                        }
                        binding.reviewsTextView.text = reviewsText

                        // Eğer yorumlar uzun ise, "Devamını Gör" metnini göster
                        if (reviewsText.length > 300) {
                            binding.showMoreTextView.visibility = View.VISIBLE
                        }

                        // Devamını Gör yazısına tıklanınca yorumları tamamını göster
                        binding.showMoreTextView.setOnClickListener {
                            binding.reviewsTextView.maxLines = Int.MAX_VALUE
                            binding.showMoreTextView.visibility = View.GONE
                            binding.showLessTextView.visibility = View.VISIBLE
                        }

                        // Daha Az Göster yazısına tıklanınca yorumları kısıtla
                        binding.showLessTextView.setOnClickListener {
                            binding.reviewsTextView.maxLines = 7
                            binding.showMoreTextView.visibility = View.VISIBLE
                            binding.showLessTextView.visibility = View.GONE
                        }

                        yorumlariVeritabaninaKayitEt(movieId, it.results)


                        analyzeSentiments(it.results)
                    }
                } else {
                    Toast.makeText(requireContext(), "Yorumlar yüklenemedi: ${response.code()}", Toast.LENGTH_SHORT).show()
                    yorumlariVeritabanindanYukle(movieId)
                }
            }

            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API çağrısı başarısız: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun analyzeSentiments(reviews: List<Review>) {
        reviews.forEach { review ->
            val reviewRequest = ReviewRequest(content = review.content)

            SentimentApiClient.instance.analyzeReview(reviewRequest).enqueue(object : Callback<SentimentResponse> {
                override fun onResponse(call: Call<SentimentResponse>, response: Response<SentimentResponse>) {
                    if (response.isSuccessful) {
                        val results = response.body()?.result ?: emptyList()
                        if (results.isNotEmpty()) {
                            val firstResult = results[0]
                            val sentiment = firstResult.label

                            binding.AnaliysResultTextView.apply {
                                val resultText = when (sentiment.uppercase()) {
                                    "POSITIVE" -> "Yorumların Analiz Sonucu: Pozitif 🎉"
                                    "NEGATIVE" -> "Yorumların Analiz Sonucu: Negatif 😞"
                                    else -> "Yorumların Analiz Sonucu: Nötr 😐"
                                }
                                text = resultText
                                visibility = View.VISIBLE

                                val color = when (sentiment.uppercase()) {
                                    "POSITIVE" -> R.color.green
                                    "NEGATIVE" -> R.color.red
                                    else -> R.color.gray
                                }
                                setTextColor(ContextCompat.getColor(binding.root.context, color))
                            }
                        }
                    } else {
                        Log.e("SentimentAnalysis", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<SentimentResponse>, t: Throwable) {
                    Log.e("SentimentAnalysis", "API çağrısı başarısız: ${t.message}")

                    Toast.makeText(
                        binding.root.context,
                        "Duygu analizi başarısız oldu: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }


    private fun yorumlariVeritabaninaKayitEt(movieId: Int, reviews: List<Review>) {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())

            // Veritabanında zaten var mı diye kontrol et
            val existingReviews = database.movieDao().getReviewsByMovieId(movieId)
            if (existingReviews.isEmpty()) {
                // Eğer yorumlar veritabanında yoksa, yeni yorumları ekle
                val reviewEntities = reviews.map { review ->
                    ReviewEntity(
                        movieId = movieId,
                        author = review.author,
                        content = review.content
                    )
                }
                database.movieDao().insertReviews(reviewEntities)
            }
        }
    }

    private fun aktorleriVeritabanindanYukle(movieId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())
            val actors = database.movieDao().getActorsByMovieId(movieId)

            if (actors.isNotEmpty()) {
                val actorNames = actors.joinToString(", ") { it.name }
                binding.actorsTextView.text = actorNames
            } else {
                binding.actorsTextView.text = "Oyuncu bilgisi bulunamadı."
            }
        }
    }

    private fun yorumlariVeritabanindanYukle(movieId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())
            val reviews = database.movieDao().getReviewsByMovieId(movieId)

            if (reviews.isNotEmpty()) {
                val reviewsText = reviews.joinToString("\n\n") { "${it.author}: ${it.content}" }
                binding.reviewsTextView.text = reviewsText

                // Yorum uzunluğu 300 karakteri aşarsa, "Devamını Gör" yazısını göster
                if (reviewsText.length > 300) {
                    binding.showMoreTextView.visibility = View.VISIBLE
                }

                // "Devamını Gör" yazısına tıklanınca yorumları tamamını göster
                binding.showMoreTextView.setOnClickListener {
                    binding.reviewsTextView.maxLines = Int.MAX_VALUE  // Tüm satırları göster
                    binding.showMoreTextView.visibility = View.GONE  // "Devamını Gör" yazısını gizle
                    binding.showLessTextView.visibility = View.VISIBLE  // "Daha Az Göster" yazısını göster
                }

                // "Daha Az Göster" yazısına tıklanınca yorumları kısıtla
                binding.showLessTextView.setOnClickListener {
                    binding.reviewsTextView.maxLines = 7  // Yalnızca 7 satır göster
                    binding.showMoreTextView.visibility = View.VISIBLE  // "Devamını Gör" yazısını tekrar göster
                    binding.showLessTextView.visibility = View.GONE  // "Daha Az Göster" yazısını gizle
                }
            } else {
                binding.reviewsTextView.text = "Yorum bulunamadı."
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}