package com.example.plantapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plantapp.network.repository.article.FireStoreRepository
import com.example.plantapp.network.repository.plant.PlantRepository
import com.example.plantapp.ui.add_plants.AddPlantViewModel
import com.example.plantapp.ui.main.article.ArticleDetailViewModel
import com.example.plantapp.ui.main.article.ArticleViewModel
import com.example.plantapp.ui.main.home.HomeViewModel
import com.example.plantapp.ui.main.profile.ProfileViewModel
import com.example.plantapp.ui.main.specie.SpeciesViewModel

class ViewModelFactory constructor(
    private val plantRepository: PlantRepository? = null,
    private val fireStoreRepository: FireStoreRepository? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> {
                if (plantRepository == null) {
                    throw IllegalArgumentException("plantRepository == null")
                } else {
                    HomeViewModel(plantRepository) as T
                }
            }

            AddPlantViewModel::class.java -> {
                if (fireStoreRepository == null) {
                    throw IllegalArgumentException("fireStoreRepository == null")
                } else {
                    AddPlantViewModel(fireStoreRepository) as T
                }
            }

            ProfileViewModel::class.java -> {
                if (fireStoreRepository == null && plantRepository == null) {
                    throw IllegalArgumentException("articleRepository == null")
                } else {
                    ProfileViewModel(fireStoreRepository!!, plantRepository!!) as T
                }
            }

            ArticleViewModel::class.java -> {
                if (fireStoreRepository == null) {
                    throw IllegalArgumentException("articleRepository == null")
                } else {
                    ArticleViewModel(fireStoreRepository) as T
                }
            }

            ArticleDetailViewModel::class.java -> {
                if (fireStoreRepository == null) {
                    throw IllegalArgumentException("plantRepository == null")
                } else {
                    ArticleDetailViewModel(fireStoreRepository) as T
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
