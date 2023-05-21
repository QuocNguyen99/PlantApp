package com.example.plantapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plantapp.network.repository.article.ArticleRepository
import com.example.plantapp.network.repository.plant.PlantRepository
import com.example.plantapp.ui.main.article.ArticleDetailViewModel
import com.example.plantapp.ui.main.article.ArticleViewModel
import com.example.plantapp.ui.main.home.HomeViewModel
import com.example.plantapp.ui.main.profile.ProfileViewModel
import com.example.plantapp.ui.main.specie.SpeciesViewModel

class ViewModelFactory constructor(
    private val plantRepository: PlantRepository? = null,
    private val articleRepository: ArticleRepository? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass) {
            HomeViewModel::class.java -> {
                if (plantRepository == null) {
                    throw IllegalArgumentException("plantRepository == null")
                } else {
                    HomeViewModel(plantRepository) as T
                }
            }

            ProfileViewModel::class.java -> {
                if (articleRepository == null) {
                    throw IllegalArgumentException("articleRepository == null")
                } else {
                    ProfileViewModel(articleRepository) as T
                }
            }

            ArticleViewModel::class.java -> {
                if (articleRepository == null) {
                    throw IllegalArgumentException("articleRepository == null")
                } else {
                    ArticleViewModel(articleRepository) as T
                }
            }

            ArticleDetailViewModel::class.java -> {
                if (articleRepository == null) {
                    throw IllegalArgumentException("plantRepository == null")
                } else {
                    ArticleDetailViewModel(articleRepository) as T
                }
            }

            SpeciesViewModel::class.java -> {
                if (plantRepository == null) {
                    throw IllegalArgumentException("SpeciesViewModel == null")
                } else {
                    SpeciesViewModel(plantRepository) as T
                }
            }

            else ->
                throw IllegalArgumentException("ViewModel Not Found")

        }
    }
}
