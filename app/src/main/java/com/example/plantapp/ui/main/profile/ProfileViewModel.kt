package com.example.plantapp.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantapp.data.model.Article
import com.example.plantapp.data.response.Plant
import com.example.plantapp.network.repository.article.ArticleRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel(private val articleRepository: ArticleRepository): ViewModel() {

    private val _collectedPlants = MutableLiveData<MutableList<Plant>>()
    val collectedPlants : LiveData<MutableList<Plant>> = _collectedPlants

    private val _collectedArticles = MutableLiveData<MutableList<Article>>()
    val collectedArticles : LiveData<MutableList<Article>> = _collectedArticles
    private val _email = Firebase.auth.currentUser?.email ?: ""

    fun getCollectedPlants() {

    }

    fun getCollectedArticle() {
        articleRepository.getArticles(
            onSuccess = {
                val collected = mutableListOf<Article>()
                it.forEach { article ->
                    article?.apply {
                        if (liked.contains(_email)) {
                            collected.add(this)
                        }
                    }
                }
                _collectedArticles.value = collected
            },
            onError = {

            }
        )
    }

    fun setLikedArticle(articleId: String, isLiked: Boolean) {
        articleRepository.likedArticle(articleId, isLiked,_email,
            onSuccess = {
                getCollectedArticle()
            },
            onError = {

            })
    }

}