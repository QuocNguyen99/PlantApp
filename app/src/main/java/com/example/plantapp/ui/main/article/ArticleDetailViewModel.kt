package com.example.plantapp.ui.main.article

import android.util.Log
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

    private val _likeCompleted = MutableLiveData(true)
    val likeCompleted : LiveData<Boolean> = _likeCompleted

    fun setLikedArticle(isLiked: Boolean) {
        article?.apply {
            _likeCompleted.value = false
            articleRepository.likedArticle(id, isLiked,
                onSuccess = {
                    _likeCompleted.value = true
                    Log.d("CheckApp", "Like success")
                },
                onError = {
                    _likeCompleted.value = true
                    Log.d("CheckApp", "Like fail")
                })
        }
    }
}