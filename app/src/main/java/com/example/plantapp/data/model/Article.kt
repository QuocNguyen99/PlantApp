package com.example.plantapp.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: String = "",
    val author: String = "",
    val content: String = "",
    val title: String = "",
    val time: Timestamp = Timestamp.now(),
    val image: String = "",
    val liked: MutableList<String> = mutableListOf(),
    val tag: MutableList<String> = mutableListOf()
) : Parcelable