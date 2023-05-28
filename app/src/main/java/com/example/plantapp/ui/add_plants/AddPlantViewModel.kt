package com.example.plantapp.ui.add_plants

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantapp.data.response.Plant
import com.example.plantapp.network.repository.article.FireStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AddPlantViewModel(
    private val fireStoreRepository: FireStoreRepository
) : ViewModel() {

    var bitmap: Bitmap? = null

    private val _saveCompleted = MutableLiveData<Boolean>(false)
    val saveCompleted: LiveData<Boolean> = _saveCompleted

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    fun addPlant(name: String, cycle: String, watering: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val plant = Plant()
            plant.scientific_name = mutableListOf(name)
            plant.other_name = mutableListOf(name)
            plant.sunlight = mutableListOf("")
            plant.scientific_name = listOf(name)
            plant.cycle = cycle
            plant.id = Date().time.toInt()
            plant.watering = watering
            bitmap?.let { bm ->
                fireStoreRepository.setPlant(
                    plant = plant,
                    bitmap = bm,
                    onError = {
                        _error.value = it.toString()
                    },
                    onSuccess = {
                        _saveCompleted.value = true
                    })
            }
        }
    }
}