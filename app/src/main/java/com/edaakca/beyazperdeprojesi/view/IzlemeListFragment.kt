package com.edaakca.beyazperdeprojesi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edaakca.beyazperdeprojesi.adapter.IzlemeListAdapter
import com.edaakca.beyazperdeprojesi.databinding.FragmentIzlemeListBinding
import com.edaakca.beyazperdeprojesi.roomdb.MovieDatabase
import com.edaakca.beyazperdeprojesi.roomdb.MovieEntity
import kotlinx.coroutines.launch

class IzlemeListFragment : Fragment() {

    private var _binding: FragmentIzlemeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var izlemeListAdapter: IzlemeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIzlemeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView ayarlarını yapıyoruz
        binding.recyclerViewIzlemeList.layoutManager = LinearLayoutManager(requireContext())

        // Favori filmleri yükleyip Adapter'ı ayarlıyoruz
        loadFavoriteMovies()
    }

    private fun loadFavoriteMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())

            // Favori filmleri alıyoruz
            val favoriteMovieList = database.movieDao().getAllFavoriteMovies()

            if (favoriteMovieList.isEmpty()) {
                binding.recyclerViewIzlemeList.visibility = View.GONE
            } else {
                binding.recyclerViewIzlemeList.visibility = View.VISIBLE
            }

            // Adapter'ı başlatıyoruz
            izlemeListAdapter = IzlemeListAdapter(favoriteMovieList) { movie, isChecked ->
                // CheckBox durumu değiştiğinde film izlenmiş olarak işaretleniyor
                movie.isWatched = isChecked
                updateMovieWatchedStatus(movie)
            }

            binding.recyclerViewIzlemeList.adapter = izlemeListAdapter
            izlemeListAdapter.notifyDataSetChanged()
        }
    }

    private fun updateMovieWatchedStatus(movie: MovieEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = MovieDatabase.getDatabase(requireContext())
            // İzleme durumu güncelleniyor
            database.movieDao().updateMovie(movie)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
