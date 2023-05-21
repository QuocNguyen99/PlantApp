package com.example.plantapp.data.response

data class Plant(
    val common_name: String = "",
    val cycle: String = "",
    val default_image: DefaultImage? = null,
    val id: Int = -1,
    val other_name: List<String>? = null,
    val scientific_name: List<String>? = null,
    val sunlight: List<Any>? = null,
    val watering: String = ""
)