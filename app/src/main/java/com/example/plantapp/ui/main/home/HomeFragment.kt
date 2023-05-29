package com.example.plantapp.ui.main.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentHomeBinding
import com.example.plantapp.network.ApiClient
import com.example.plantapp.network.repository.plant.PlantRepository
import com.example.plantapp.ui.ViewModelFactory
import com.example.plantapp.ui.main.MainFragmentDirections
import com.example.plantapp.ui.main.specie.SpeciesViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelSpecies: SpeciesViewModel

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
        viewModelSpecies = ViewModelProvider(requireActivity(), ViewModelFactory(mainRepository))[SpeciesViewModel::class.java]

        initData()
        initView()
        initObserve()

        binding.itemOne.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToAddPlantFragment())
        }

        binding.itemThree.setOnClickListener {
            Log.d("setOnClickListener", "onViewCreated: ")
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToArticleFragment())
        }
        binding.itemTwo.setOnClickListener {
            findNavController().navigate(R.id.speciesFragment)
        }
    }

    var indexPage = 1
    private fun initObserve() {
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            Log.d("HomeFragment", "initObserve: ${plants.size}")
            binding.titlePlantTypes.isVisible = true
            plantTypeAdapter.submitList(plants)
//            val db = Firebase.firestore
//            plants.forEachIndexed { index, plant ->
//                if (index == 2) return@forEachIndexed
//                db.collection("species").document(System.currentTimeMillis().toString())
//                    .set(plant)
//                    .addOnSuccessListener {
//                        Log.d("TAG", "DocumentSnapshot successfully written!")
//
//                    }
//                    .addOnFailureListener { e ->
//                        Log.w("TAG", "Error writing document", e)
//                    }
//            }
//
//            if (indexPage == 200) return@observe
//            val inputStream = resources.openRawResource(R.raw.config)
//            val text = inputStream.bufferedReader().use { it.readText() }
//            val key = text.substringAfter("=")
//            viewModel.getPlant(indexPage, key)
//            indexPage += 1
        }

        viewModel.cycles.observe(viewLifecycleOwner) {
            Log.d("TAG", "initObserve: $it")
            binding.titlePhoto.isVisible = true
            plantCycleAdapter.submitList(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.rcPlantType.adapter = plantTypeAdapter
        binding.rcPhoto.adapter = plantCycleAdapter

        val snapHelperPlant: SnapHelper = LinearSnapHelper()
        snapHelperPlant.attachToRecyclerView(binding.rcPlantType)

        val snapHelperPhoto: SnapHelper = LinearSnapHelper()
        snapHelperPhoto.attachToRecyclerView(binding.rcPhoto)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val fullName = sharedPref.getString("fullname", "")
        val indexOf = fullName?.lastIndexOf(" ")
        binding.tvName.text = "Hello " + indexOf?.let { fullName?.substring(it, fullName.toCharArray().size) } + ","
    }

    private fun initData() {
        val inputStream = resources.openRawResource(R.raw.config)
        val text = inputStream.bufferedReader().use { it.readText() }
        val key = text.substringAfter("=")
        viewModel.getPlant(1, key)
        viewModel.getCycle(3, key)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModelSpecies.getSpecies()
            withContext(Dispatchers.Main) {
                binding.progressBar.isVisible = true
                binding.root.isEnabled = false
                Thread.sleep(4000)
                binding.progressBar.isVisible = false
                binding.root.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}