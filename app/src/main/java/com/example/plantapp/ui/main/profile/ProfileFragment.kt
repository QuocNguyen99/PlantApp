package com.example.plantapp.ui.main.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentProfileBinding
import com.example.plantapp.network.ApiClient
import com.example.plantapp.network.repository.article.FireStoreRepository
import com.example.plantapp.network.repository.plant.PlantRepository
import com.example.plantapp.ui.ViewModelFactory
import com.example.plantapp.ui.main.MainFragmentDirections
import com.example.plantapp.ui.main.article.ArticleAdapter

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private val collectedPlantsAdapter = CollectedPlantsAdapter()
    private val collectedArticleAdapter = ArticleAdapter()
    private var sharedPref: SharedPreferences? = null
    var key = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("ProfileFragment", "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ProfileFragment", "onResume: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val service = ApiClient.plantService
        val mainRepository = PlantRepository(service)
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(mainRepository, fireStoreRepository = FireStoreRepository())
        )[ProfileViewModel::class.java]

        val inputStream = resources.openRawResource(R.raw.config)
        val text = inputStream.bufferedReader().use { it.readText() }
        key = text.substringAfter("=")
        initEvent()
        initData()
        initObserve()
    }

    private fun initObserve() {
        viewModel.collectedPlants.observe(viewLifecycleOwner) {
            if (it.size == 0) return@observe
            collectedPlantsAdapter.submitList(it)
        }

        viewModel.collectedArticles.observe(viewLifecycleOwner) {
            collectedArticleAdapter.submitList(it)
        }
    }

    private fun initData() {
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        val liked = sharedPref?.getString("liked", "")
        sharedPref?.registerOnSharedPreferenceChangeListener(listener)
        binding.rlvCollectedPlants.adapter = collectedPlantsAdapter
        binding.rlvCollectedArticle.adapter = collectedArticleAdapter
        if (liked != null) {
            viewModel.getCollectedPlants(liked, key)
        }
        viewModel.getCollectedArticle()

        val fullName = sharedPref?.getString("fullname", "")
        binding.tvName.text = fullName
    }

    private fun initEvent() {
        binding.apply {
            btnArticle.setOnClickListener {
                btnArticle.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_item_profile)
                btnArticle.setTextColor(requireContext().getColor(R.color.white))
                btnSpecies.background = ContextCompat.getDrawable(requireContext(), R.color.white)
                btnSpecies.setTextColor(requireContext().getColor(R.color.colorText))
                setupCollected(false)
                rlvCollectedPlants.isVisible = false
            }
            btnSpecies.setOnClickListener {
                btnSpecies.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_item_profile)
                btnSpecies.setTextColor(requireContext().getColor(R.color.white))
                btnArticle.background = ContextCompat.getDrawable(requireContext(), R.color.white)
                btnArticle.setTextColor(requireContext().getColor(R.color.colorText))
                setupCollected(true)
                rlvCollectedPlants.isVisible = true
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
                val liked = sharedPref.getString("liked", "")
                if (liked != null) {
                    viewModel.getCollectedPlants(liked, key)
                }
            }
        }

        collectedArticleAdapter.onLiked = { articleId, isLiked ->
            viewModel.setLikedArticle(articleId, isLiked)
        }

        collectedArticleAdapter.onClick = {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToArticleDetailFragment(it))
        }

        collectedPlantsAdapter.onClick = {
            viewModel.itemDetail.value = it
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToSpecieDetailFragment(-1))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedPref?.unregisterOnSharedPreferenceChangeListener(listener)
        Log.d("ProfileFragment", "onDestroyView: ")
    }

    private fun setupCollected(isShownSpecies: Boolean) {
        binding.groupSpeciesView.visibility = if (isShownSpecies) View.VISIBLE else View.GONE
        binding.groupArticleView.visibility = if (isShownSpecies) View.GONE else View.VISIBLE
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, k ->
        if (k == "liked") {
            val liked = sharedPreferences.getString("liked", "")
            if (liked != null) {
                viewModel.getCollectedPlants(liked, key)
            }
        }
    }
}