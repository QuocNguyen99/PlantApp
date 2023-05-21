package com.example.plantapp.ui.main.specie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantapp.data.model.DetailSpecie
import com.example.plantapp.data.model.Specie
import com.example.plantapp.network.repository.plant.PlantRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpeciesViewModel(var plantRepository: PlantRepository? = null) : ViewModel() {
    private val db = Firebase.firestore

    private val _species = MutableLiveData<MutableList<Specie?>>()
    val species: LiveData<MutableList<Specie?>> = _species

    private val _specieDetailList = MutableLiveData<MutableList<DetailSpecie>>(mutableListOf())
    val specieDetailList: LiveData<MutableList<DetailSpecie>> = _specieDetailList

    fun getSpecies() {
        db.collection("species")
            .get()
            .addOnSuccessListener { documents ->
                val newList = mutableListOf<Specie>()
                Log.d("TAG", "initData ${documents.size()}")
                for (document in documents) {
                    val index = document.data["scientific_name"].toString().indexOf(" ")
                    val type = document.data["scientific_name"].toString().substring(1, index)
                    newList.add(
                        Specie(
                            type,
                            document.data["scientific_name"].toString().toCharArray()[1].toString(),
                            document.toObject()
                        )
                    )
                }
                val sortedList = newList.sortedWith(compareBy { it.type })
                _species.postValue(sortedList.toMutableList())
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    fun getDetailSpecies(list: MutableList<Specie?>, key: String) {
        _specieDetailList.value = mutableListOf()
        if (list.size >= 10) {
            list.subList(0, 10).forEach {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        it?.let {
                            val data = plantRepository?.getDetailSpecie(it.specie.id, key)
                            Log.d("TAG", "getDetailSpecies: ${data?.id}")
                            val tempList = _specieDetailList.value
                            if (data != null) {
                                tempList!!.add(data)
                            }
                            tempList.let { listTemp ->
                                _specieDetailList.postValue(listTemp)
                            }
                        }
                    } catch (ex: Exception) {
                        Log.e("TAG", "getDetailSpecies: ${ex.message}")
                    }
                }
            }
        } else {
            list.forEach {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        it?.let {
                            val data = plantRepository?.getDetailSpecie(it.specie.id, key)
                            Log.d("TAG", "getDetailSpecies: ${data?.id}")
                            val tempList = _specieDetailList.value
                            if (data != null) {
                                tempList!!.add(data)
                            }
                            tempList.let { listTemp ->
                                _specieDetailList.postValue(listTemp)
                            }
                        }
                    } catch (ex: Exception) {
                        Log.e("TAG", "getDetailSpecies: ${ex.message}")
                    }
                }
            }
        }
    }
}