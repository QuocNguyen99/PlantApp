package com.example.plantapp.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.plantapp.R
import com.example.plantapp.databinding.FragmentHomeBinding
import com.example.plantapp.databinding.FragmentProfileBinding
import com.example.plantapp.network.repository.article.ArticleRepository
import com.example.plantapp.ui.ViewModelFactory
import com.example.plantapp.ui.main.MainFragmentDirections
import com.example.plantapp.ui.main.article.ArticleAdapter
import com.example.plantapp.ui.main.article.ArticleDetailFragmentArgs
import com.example.plantapp.ui.main.article.ArticleFragmentDirections
import com.example.plantapp.ui.main.home.HomeViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private val collectedPlantsAdapter = CollectedPlantsAdapter()
    private val collectedArticleAdapter = ArticleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(articleRepository = ArticleRepository())
        )[ProfileViewModel::class.java]
        initEvent()
        initData()
        initObserve()
    }

    private fun initObserve() {
        viewModel.collectedPlants.observe(viewLifecycleOwner) {
            collectedPlantsAdapter.submitList(it)
        }

        viewModel.collectedArticles.observe(viewLifecycleOwner) {
            collectedArticleAdapter.submitList(it)
        }
    }

    private fun initData() {
        binding.rlvCollectedPlants.adapter = collectedPlantsAdapter
        binding.rlvCollectedArticle.adapter = collectedArticleAdapter
        viewModel.getCollectedPlants()
        viewModel.getCollectedArticle()
    }

    override fun onResume() {
        super.onResume()
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
            }
            btnSpecies.setOnClickListener {
                btnSpecies.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_item_profile)
                btnSpecies.setTextColor(requireContext().getColor(R.color.white))
                btnArticle.background = ContextCompat.getDrawable(requireContext(), R.color.white)
                btnArticle.setTextColor(requireContext().getColor(R.color.colorText))
                setupCollected(true)
            }
        }

        collectedArticleAdapter.onLiked = { articleId, _, isLiked ->
            viewModel.setLikedArticle(articleId, isLiked)
        }

        collectedArticleAdapter.onClick = {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToArticleDetailFragment(it))
        }
    }

    private fun setupCollected(isShownSpecies: Boolean) {
        binding.groupSpeciesView.visibility = if (isShownSpecies) View.VISIBLE else View.GONE
        binding.groupArticleView.visibility = if (isShownSpecies) View.GONE else View.VISIBLE
    }
}