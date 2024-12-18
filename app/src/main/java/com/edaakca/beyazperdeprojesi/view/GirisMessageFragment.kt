package com.edaakca.beyazperdeprojesi.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.databinding.FragmentGirisMessageBinding

class GirisMessageFragment : Fragment() {

    private var _binding: FragmentGirisMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGirisMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonGiris.setOnClickListener {
            findNavController().navigate(R.id.action_girisMessageFragment_to_profileFragment)

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}