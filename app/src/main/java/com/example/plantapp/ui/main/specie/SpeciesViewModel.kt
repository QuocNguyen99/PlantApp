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

    init {
        addRealtimeSpecies()
    }

    fun getSpecies() {
        db.collection("species")
            .get()
            .addOnSuccessListener { documents ->
                try {
                    val newList = mutableListOf<Specie>()
                    Log.d("TAG", "initData ${documents.size()}")
                    for (document in documents) {
                        Log.d("TAG", "document.id: ${document.data["id"]}")
                        val index = document.data["scientific_name"].toString().indexOf(" ")
                        val type = if (index == -1) {
                            document.data["scientific_name"].toString()
                                .substring(1, document.data["scientific_name"].toString().toCharArray().size - 1)
                        } else {
                            document.data["scientific_name"].toString().substring(1, index)
                        }
                        Log.d("TAG", "type: $type")
                        newList.add(
                            Specie(
                                type,
                                document.data["scientific_name"].toString().toCharArray()[1].toString().uppercase(),
                                document.toObject()
                            )
                        )
                    }
                    val sortedList = newList.sortedWith(compareBy { it.alphabet })
                    viewModelScope.launch(Dispatchers.Main.immediate) {
                        Log.d("sortedList", "sortedList: ${sortedList.size} ")
                        _species.postValue(sortedList.toMutableList())
                    }
                } catch (ex: Exception) {
                    Log.e("TAG", "getSpeciesError: ${ex.cause}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ${exception.cause}", exception)
            }
    }

    private fun addRealtimeSpecies() {
        db.collection("species").addSnapshotListener {
                _, _ ->
            _species.value = mutableListOf()
            getSpecies()
        }
    }

    fun testData(list: MutableList<Specie>) {
        list.forEach {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val data = plantRepository?.getDetailSpecie(it.specie.id, "sk-jSI0645f5cdfebfd3920")
                    Log.d("TAG", "getDetailSpecies: ${data?.id}")
                    db.collection("detail_specie").document(System.currentTimeMillis().toString())
                        .set(data!!)
                        .addOnSuccessListener {
                            Log.d("TAG", "DocumentSnapshot successfully written!")

                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error writing document", e)
                        }
                } catch (ex: Exception) {
                    Log.e("TAG", "getDetailSpecies: ${ex.message}")
                }
            }
        }
    }

    fun getDetailSpecies(list: MutableList<Specie?>) {
        _specieDetailList.value = mutableListOf()
        if (list.size >= 10) {
            list.subList(0, 10).forEach { item ->
                db.collection("detail_specie")
                    .whereEqualTo("id", item?.specie?.id)
                    .get()
                    .addOnSuccessListener { documents ->
                        try {
                            for (document in documents) {
                                val newList = _specieDetailList.value
                                newList?.add(document.toObject())
                                newList?.let { list ->
                                    _specieDetailList.postValue(list)
                                }
                            }
                        } catch (ex: Exception) {
                            Log.e("TAG", "getDetailSpecies: ${ex.message}")
                        }
                    }
            }
        } else {
            list.forEach { item ->
                db.collection("detail_specie")
                    .whereEqualTo("id", item?.specie?.id)
                    .get()
                    .addOnSuccessListener { documents ->
                        try {
                            for (document in documents) {
                                val newList = _specieDetailList.value
                                newList?.add(document.toObject())
                                newList?.let { list ->
                                    _specieDetailList.postValue(list)
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