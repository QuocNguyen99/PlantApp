package com.example.plantapp.ui.main.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantapp.data.model.Article
import com.example.plantapp.data.response.Plant
import com.example.plantapp.network.repository.article.ArticleRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ArticleViewModel(
    private val articleRepository: ArticleRepository
): ViewModel() {

    private val _articles = MutableLiveData<List<Article?>>()
    val articles : LiveData<List<Article?>> = _articles

    fun getArticles() {
        articleRepository.getArticles(
            {
                _articles.value = it
            },
            {

            }
        )
    }

    fun setLikedArticle(articleId: String, email: String, isLiked: Boolean) {
        articleRepository.likedArticle(articleId, isLiked, email,
        onSuccess = {
            getArticles()
        },
        onError = {

        })
    }
}