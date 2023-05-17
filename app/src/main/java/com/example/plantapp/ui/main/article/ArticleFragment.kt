package com.example.plantapp.ui.main.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.plantapp.databinding.FragmentArticleBinding
import com.example.plantapp.network.repository.article.ArticleRepository
import com.example.plantapp.ui.ViewModelFactory

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArticleViewModel
    private val articleAdapter = ArticleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(articleRepository = ArticleRepository())
        )[ArticleViewModel::class.java]
        initEvent()
        initData()
        initObserve()
    }

    private fun initObserve() {
        viewModel.articles.observe(viewLifecycleOwner) {
            articleAdapter.submitList(it)
        }
    }

    private fun initData() {
        binding.rlvArticle.adapter = articleAdapter
        viewModel.getArticles()
    }

    private fun initEvent() {
        articleAdapter.onClick = {
            val action = ArticleFragmentDirections.actionArticleFragmentToArticleDetailFragment(it)
            findNavController().navigate(action)
        }

        articleAdapter.onLiked = { articleId, email, isLiked ->
            viewModel.setLikedArticle(articleId, email, isLiked)
        }

        binding.imvBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}