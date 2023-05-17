package com.example.plantapp.ui.main.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantapp.R
import com.example.plantapp.data.model.Article
import com.example.plantapp.databinding.FragmentArticleDetailBinding
import com.example.plantapp.network.repository.article.ArticleRepository
import com.example.plantapp.ui.ViewModelFactory
import com.example.plantapp.utils.format
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ArticleDetailFragment: Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArticleDetailViewModel
    private lateinit var article: Article
    private val args: ArticleDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        article = args.article
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(articleRepository = ArticleRepository())
        )[ArticleDetailViewModel::class.java]
        viewModel.article = article
        initEvent()
        initData()
        initObserve()
    }

    private fun initObserve() {

    }

    private fun initData() {
        binding.apply {
            Glide.with(root.context).load(article.image).into(background)
            title.text = article.title
            content.text = article.content
            author.text = article.author
            time.text = article.time.format()

            val email = Firebase.auth.currentUser?.email ?: ""
            if (article.liked.contains(email)) {
                imvLiked.setImageResource(R.drawable.background_like)
            } else {
                imvLiked.setImageResource(R.drawable.background_unlike)
            }
        }
    }

    private fun initEvent() {
        binding.imvBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imvLiked.setOnClickListener {
            viewModel.setLikedArticle()
        }

        binding.imvLiked.setOnClickListener {
            val email = Firebase.auth.currentUser?.email ?: ""
            if (article.liked.contains(email)) {
                binding.imvLiked.setImageResource(R.drawable.background_unlike)
            } else {
                binding.imvLiked.setImageResource(R.drawable.background_like)
            }
            viewModel.setLikedArticle()
        }
    }

}