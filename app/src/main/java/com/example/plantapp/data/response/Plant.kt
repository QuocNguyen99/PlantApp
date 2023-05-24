package com.example.plantapp.data.response

data class Plant(
    var common_name: String = "",
    var cycle: String = "",
    var default_image: DefaultImage? = null,
    var id: Int = -1,
    var other_name: List<String>? = null,
    var scientific_name: List<String>? = null,
    var sunlight: List<Any>? = null,
    var watering: String = ""
)