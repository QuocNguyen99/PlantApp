package com.example.plantapp.network.repository.plant

class PlantRepository(private val plantService: PlantService) {
    suspend fun getPlants(page: Int, key: String) = plantService.getListPlant(page, key)

    suspend fun getDetailSpecie(id: Int, key: String) = plantService.getDetailSpecie(id, key)

}