package com.example.plantapp.ui.add_plants

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantapp.data.response.Plant
import com.example.plantapp.network.repository.article.FireStoreRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    private val db = Firebase.firestore

    private val _type = MutableLiveData<HashSet<String>>()
    val type: LiveData<HashSet<String>> = _type

    init {
        getSpecies()
    }

    fun addPlant(
        name: String,
        type: String,
        cycle: String,
        watering: String,
        description: String
    ) {
        if (bitmap == null) {
            _error.value = "Image is null"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val plant = Plant()
            plant.common_name = "$type $name"
            plant.other_name = mutableListOf("$type $name")
            plant.sunlight = mutableListOf("")
            plant.scientific_name = listOf("$type $name")
            plant.cycle = cycle
            plant.id = Date().time.toInt()
            plant.watering = watering
            bitmap?.let { bm ->
                fireStoreRepository.setPlant(
                    plant = plant,
                    bitmap = bm,
                    description = description,
                    onError = {
                        _error.value = it.toString()
                    },
                    onSuccess = {
                        _saveCompleted.value = true
                    })
            }
        }
    }

    private fun getSpecies() {
        db.collection("species")
            .get()
            .addOnSuccessListener { documents ->
                try {
                    val newList = HashSet<String>()
                    Log.d("TAG", "initData ${documents.size()}")
                    for (document in documents) {
                        Log.d("TAG", "document.id: ${document.data["id"]}")
                        val index = document.data["scientific_name"].toString().indexOf(" ")
                        val type = if (index == -1) {
                            document.data["scientific_name"].toString()
                                .substring(
                                    1,
                                    document.data["scientific_name"].toString()
                                        .toCharArray().size - 1
                                )
                        } else {
                            document.data["scientific_name"].toString().substring(1, index)
                        }
                        Log.d("TAG", "type: $type")
                        if (type.trim().isNotEmpty()) {
                            newList.add(type)
                        }
                    }
                    _type.value = newList
                } catch (ex: Exception) {
                    Log.e("TAG", "getSpeciesError: ${ex.cause}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ${exception.cause}", exception)
            }
    }

}