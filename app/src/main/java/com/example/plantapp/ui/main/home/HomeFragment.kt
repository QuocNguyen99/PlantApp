package com.example.plantapp.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentHomeBinding
import com.example.plantapp.network.ApiClient
import com.example.plantapp.network.repository.plant.PlantRepository
import com.example.plantapp.ui.ViewModelFactory


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private val plantTypeAdapter = PlantTypeAdapter()
    private val plantCycleAdapter = PlantCycleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val retrofitService = ApiClient.plantService
        val mainRepository = PlantRepository(retrofitService)
        viewModel = ViewModelProvider(this, ViewModelFactory(mainRepository))[HomeViewModel::class.java]

        initData()
        initView()
        initObserve()

        binding.itemOne.setOnClickListener {
            Log.d("setOnClickListener", "onViewCreated: ")
        }
        binding.itemThree.setOnClickListener {
            Log.d("setOnClickListener", "onViewCreated: ")
            findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToArticleFragment())
        }
        binding.itemTwo.setOnClickListener {
            Log.d("setOnClickListener", "onViewCreated: ")
        }
    }

    private fun initObserve() {
        viewModel.plants.observe(viewLifecycleOwner) {
            Log.d("TAG", "initObserve: $it")
            binding.titlePlantTypes.isVisible = true
            plantTypeAdapter.submitList(it)
        }

        viewModel.cycles.observe(viewLifecycleOwner) {
            Log.d("TAG", "initObserve: $it")
            binding.titlePhoto.isVisible = true
            plantCycleAdapter.submitList(it)
        }

    }

    private fun initView() {
        binding.rcPlantType.adapter = plantTypeAdapter
        binding.rcPhoto.adapter = plantCycleAdapter

        val snapHelperPlant: SnapHelper = LinearSnapHelper()
        snapHelperPlant.attachToRecyclerView(binding.rcPlantType)

        val snapHelperPhoto: SnapHelper = LinearSnapHelper()
        snapHelperPhoto.attachToRecyclerView(binding.rcPhoto)
    }

    private fun initData() {
        val inputStream = resources.openRawResource(R.raw.config)
        val text = inputStream.bufferedReader().use { it.readText() }
        val key = text.substringAfter("=")
        viewModel.getPlant(1, key)
        viewModel.getCycle(3, key)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}