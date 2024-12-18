package com.edaakca.beyazperdeprojesi.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edaakca.beyazperdeprojesi.adapter.AramaMovieAdapter
import com.edaakca.beyazperdeprojesi.adapter.OnMovieClickListener
import com.edaakca.beyazperdeprojesi.databinding.FragmentAramaBinding
import com.edaakca.beyazperdeprojesi.model.Movie
import com.edaakca.beyazperdeprojesi.model.MovieResponse
import com.edaakca.beyazperdeprojesi.service.RetrofitClient
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AramaFragment : Fragment(), OnMovieClickListener {

    private val turMap = mapOf(
        "Aksiyon" to 28,
        "Macera" to 12,
        "Komedi" to 35,
        "Dram" to 18,
        "Gerilim" to 53,
        "Korku" to 27,
        "Bilim Kurgu" to 878,
        "Tüm Türler" to 0
    )

    private var _binding: FragmentAramaBinding? = null
    private val binding get() = _binding!!

    private var sayfa = 1
    private var toplamSayfaSayisi = 5
    private val tumFilmler = mutableListOf<Movie>()

    private lateinit var aramaEditText: EditText
    private lateinit var turChipGrubu: ChipGroup
    private lateinit var aramaRecyclerView: RecyclerView
    private lateinit var adapter: AramaMovieAdapter

    private val apiKey = "c2b19da13b4def83efd434d1e00f7c6f"
    private val handler = Handler(Looper.getMainLooper())
    private var aramaRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAramaBinding.inflate(inflater, container, false)

        arayuzHazirla()
        dinleyicileriAyarla()

        return binding.root
    }

    private fun arayuzHazirla() {
        aramaEditText = binding.searchEditText
        turChipGrubu = binding.genreChipGroup
        aramaRecyclerView = binding.aramaRecyclerView

        adapter = AramaMovieAdapter(emptyList(), this)
        aramaRecyclerView.adapter = adapter
        aramaRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        chipGrubunuDoldur()
    }

    private fun chipGrubunuDoldur() {
        turMap.keys.forEach { tur ->
            val chip = Chip(requireContext()).apply {
                text = tur
                isCheckable = true
                setOnClickListener {
                    turSeciminiIsle()
                }
            }
            turChipGrubu.addView(chip)
        }
    }

    private fun dinleyicileriAyarla() {
        aramaEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aramaZamanlayici(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun turSeciminiIsle() {
        val secilenChip = turChipGrubu.checkedChipIds.firstOrNull()?.let {
            turChipGrubu.findViewById<Chip>(it)
        }
        val turId = turMap[secilenChip?.text.toString()] ?: 0
        filmleriAra(aramaEditText.text.toString(), turId.toString())
    }

    private fun aramaZamanlayici(sorgu: String) {
        aramaRunnable?.let { handler.removeCallbacks(it) }
        aramaRunnable = Runnable {
            turSeciminiIsle()
        }
        handler.postDelayed(aramaRunnable!!, 300)
    }

    private fun filmleriAra(sorgu: String, turId: String) {
        tumFilmler.clear()
        for (sayfa in 1..toplamSayfaSayisi) {
            val call = if (sorgu.isNotEmpty()) {
                RetrofitClient.instance.searchMovies(apiKey, sorgu, "tr-TR")
            } else if (turId == "0") {
                RetrofitClient.instance.discoverMovies(apiKey, null, null, "tr-TR", sayfa)
            } else {
                RetrofitClient.instance.discoverMovies(apiKey, null, turId, "tr-TR", sayfa)
            }

            call.enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.results?.let { filmler ->
                            tumFilmler.addAll(filmler)
                            if (sayfa == toplamSayfaSayisi) {
                                adapter.filmleriGuncelle(tumFilmler)
                            }
                        }
                    } else {
                        Toast.makeText(context, "Film bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Toast.makeText(context, "Hata: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onFilmClick(film: Movie) {
        val action = AramaFragmentDirections.actionAramaFragmentToFilmDetayFragment(film)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
