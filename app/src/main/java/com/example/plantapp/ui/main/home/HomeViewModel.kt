package com.example.plantapp.ui.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plantapp.data.response.Plant
import com.example.plantapp.data.response.PlantResponse
import com.example.plantapp.network.repository.plant.PlantRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel(private val plantRepository: PlantRepository) : ViewModel() {

    private val errorMessage = MutableLiveData<String>()

    private val _plants = MutableLiveData<MutableList<Plant>>()
    val plants: LiveData<MutableList<Plant>> = _plants

    private val _cycles = MutableLiveData<MutableList<Plant>>()
    val cycles: LiveData<MutableList<Plant>> = _cycles

    private var jobGetPlant: Job? = null
    private var jobGetCycle: Job? = null

    fun getPlant(page: Int, key: String) {
        jobGetPlant = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val data = plantRepository.getPlants(page, key)
                Log.d("TAG", "Error get plant: ${data}")
                _plants.postValue(data.data)
            } catch (ex: Exception) {
                Log.d("TAG", "Error get plant: ${ex.message}")
            }
        }
    }

    fun getCycle(page: Int, key: String) {
        jobGetCycle = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val data = plantRepository.getPlants(page, key)
                Log.d("TAG", "Error get plant: ${data}")
                _cycles.postValue(data.data)
            } catch (ex: Exception) {
                Log.d("TAG", "Error get plant: ${ex.message}")
            }
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private fun onError(message: String) {
        errorMessage.value = message
    }

    override fun onCleared() {
        super.onCleared()
        jobGetPlant?.cancel()
        jobGetCycle?.cancel()
    }
}

class HomeViewModelFactory constructor(private val plantRepository: PlantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(plantRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
