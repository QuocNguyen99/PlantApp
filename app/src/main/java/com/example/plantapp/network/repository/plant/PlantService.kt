package com.example.plantapp.network.repository.plant

import com.example.plantapp.data.model.DetailSpecie
import com.example.plantapp.data.response.Plant
import com.example.plantapp.data.response.PlantResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlantService {
    @GET("api/species-list")
    suspend fun getListPlant(
        @Query("page") page: Int,
        @Query("key") key: String
    ): PlantResponse

    @GET("api/species/details/{id}")
    suspend fun getDetailSpecie(
        @Path("id") id: Int,
        @Query("key") key: String,
    ): DetailSpecie
}