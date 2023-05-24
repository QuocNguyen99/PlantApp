package com.example.plantapp.data.response

data class Plant(
    var common_name: String = "",
    var cycle: String = "",
    var default_image: DefaultImage? = null,
    val id: Int = -1,
    val other_name: List<String>? = null,
    val scientific_name: List<String>? = null,
    val sunlight: List<Any>? = null,
    var watering: String = ""
)