package com.example.plantapp.ui.main.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantapp.data.model.Article
import com.example.plantapp.network.repository.article.FireStoreRepository

class ArticleViewModel(
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {

    private val _articles = MutableLiveData<List<Article?>>()
    val articles : LiveData<List<Article?>> = _articles

    fun getArticles() {
        fireStoreRepository.getArticles(
            {
                _articles.value = it
            },
            {

            }
        )
    }

    fun setLikedArticle(articleId: String, isLiked: Boolean) {
        fireStoreRepository.likedArticle(articleId, isLiked,
        onSuccess = {
            getArticles()
        },
        onError = {

        })
    }
}