package com.example.plantapp.ui.main.specie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plantapp.R
import com.example.plantapp.data.model.Specie
import com.example.plantapp.data.response.DefaultImage
import com.example.plantapp.data.response.Plant
import com.example.plantapp.databinding.FragmentMainBinding
import com.example.plantapp.databinding.FragmentSpeciesBinding
import com.example.plantapp.network.ApiClient
import com.example.plantapp.network.repository.plant.PlantRepository
import com.example.plantapp.ui.ViewModelFactory
import com.example.plantapp.ui.main.home.HomeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SpeciesFragment : Fragment() {

    private var _binding: FragmentSpeciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SpeciesViewModel

    private val adapter = SpeciesAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpeciesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val service = ApiClient.plantService
        val mainRepository = PlantRepository(service)
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(mainRepository)
        )[SpeciesViewModel::class.java]

        initView()
        initEvent()
        initObserve()
    }

    private fun initObserve() {
        viewModel.species.observe(viewLifecycleOwner) {
            if (it.size == 0) return@observe
            val listWithOutDuplicate = mutableListOf<Specie>()
            it.forEachIndexed { index, specie ->
                specie?.let { item ->
                    if (index == 0) {
                        listWithOutDuplicate.add(item)
                    } else if (index != -1 && listWithOutDuplicate.find { specie -> specie.type == item.type } == null) {
                        listWithOutDuplicate.add(specie)
                    }
                }
            }
            Log.d("forEachIndexed", "initObserve: ${listWithOutDuplicate.size}")
            adapter.submitList(listWithOutDuplicate)
        }
    }

    private fun initView() {
        binding.rcSpecies.adapter = adapter
    }

    private fun initEvent() {
        adapter.onItemClick = {
            findNavController().navigate(
                SpeciesFragmentDirections.actionSpeciesFragmentToSpeciesDetailListFragment(
                    it
                )
            )
        }

        binding.imvBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}