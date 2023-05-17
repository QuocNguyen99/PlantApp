package com.example.plantapp.ui.main.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantapp.data.model.Article
import com.example.plantapp.data.response.Plant
import com.example.plantapp.network.repository.article.ArticleRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ArticleDetailViewModel(
    private val articleRepository: ArticleRepository
): ViewModel() {

    var article: Article? = null

    fun setLikedArticle() {
        article?.apply {
            val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
            val isLiked = !liked.contains(email)
            articleRepository.likedArticle(id, isLiked, email,
                onSuccess = {

                },
                onError = {

                })
        }
    }
}