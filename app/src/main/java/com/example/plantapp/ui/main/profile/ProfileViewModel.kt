package com.example.plantapp.ui.main.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantapp.data.model.Article
import com.example.plantapp.data.model.DetailSpecie
import com.example.plantapp.network.repository.article.FireStoreRepository
import com.example.plantapp.network.repository.plant.PlantRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(private val fireStoreRepository: FireStoreRepository, private val plantRepository: PlantRepository) : ViewModel() {

    private val _collectedPlants = MutableLiveData<MutableList<DetailSpecie>>()
    val collectedPlants: LiveData<MutableList<DetailSpecie>> = _collectedPlants

     val itemDetail = MutableLiveData<DetailSpecie>()

    private val _collectedArticles = MutableLiveData<MutableList<Article>>()
    val collectedArticles: LiveData<MutableList<Article>> = _collectedArticles
    private val _email = Firebase.auth.currentUser?.email ?: ""

    fun getCollectedPlants(liked: String, key: String) {
        val tmp = liked.replace("\\s+".toRegex(), " ").trim()
        val list = tmp.split(" ")
        _collectedPlants.value = mutableListOf()
        list.forEach {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    it.let {
                        val data = plantRepository.getDetailSpecie(it.trim().toInt(), key)
                        Log.d("TAG", "getDetailSpecies: ${data.id}")
                        val tempList = _collectedPlants.value
                        tempList!!.add(data)
                        tempList.let { listTemp ->
                            _collectedPlants.postValue(listTemp)
                        }
                    }
                } catch (ex: Exception) {
                    Log.e("TAG", "getDetailSpecies: ${ex.message}")
                }
            }
        }
    }

    fun getCollectedArticle() {
        fireStoreRepository.getArticlesRealTime(
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
        fireStoreRepository.likedArticle(articleId, isLiked,
            onSuccess = {
                getCollectedArticle()
            },
            onError = {

            })
    }

}