package com.edaakca.beyazperdeprojesi.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edaakca.beyazperdeprojesi.adapter.FavorilerAdapter
import com.edaakca.beyazperdeprojesi.databinding.FragmentFavorilerBinding
import com.edaakca.beyazperdeprojesi.roomdb.MovieDatabase
import com.edaakca.beyazperdeprojesi.roomdb.MovieEntity
import kotlinx.coroutines.launch

// FavorilerFragment.kt
class FavorilerFragment : Fragment() {

    private var _binding: FragmentFavorilerBinding? = null
    private val binding get() = _binding!!

    private lateinit var favorilerAdapter: FavorilerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavorilerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewFavoriler.layoutManager = LinearLayoutManager(requireContext())

        loadFavoriteMovies()

        checkUserLoginAndLoadFavorites()
    }

    override fun onResume() {
        super.onResume()
        loadFavoriteMovies()
    }

    private fun loadFavoriteMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())
            val favoriteMovies = database.movieDao().getAllFavoriteMovies()

            if (favoriteMovies.isEmpty()) {
                binding.recyclerViewFavoriler.visibility = View.GONE
            } else {
                binding.recyclerViewFavoriler.visibility = View.VISIBLE
            }

            favorilerAdapter = FavorilerAdapter(favoriteMovies) { movie ->
                removeFavorite(movie)
            }

            binding.recyclerViewFavoriler.adapter = favorilerAdapter
            favorilerAdapter.notifyDataSetChanged()
        }
    }

    private fun removeFavorite(movie: MovieEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())
            database.movieDao().deleteMovie(movie)

            // Güncellenmiş listeyi yükleyin
            loadFavoriteMovies()
        }
    }
    // Kullanıcı giriş yaptı mı kontrol et ve favorileri yükle
    private fun checkUserLoginAndLoadFavorites() {
        val isUserLoggedIn = checkIfUserIsLoggedIn() // Gerçek giriş kontrolünü burada yapın

        if (isUserLoggedIn) {
            loadFavoriteMovies()
        } else {
            binding.recyclerViewFavoriler.visibility = View.GONE
            Toast.makeText(requireContext(), "Giriş yapmalısınız", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIfUserIsLoggedIn(): Boolean {

        val sharedPreferences = requireContext().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
