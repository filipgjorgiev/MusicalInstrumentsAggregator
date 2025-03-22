package com.example.musicalinstrumentsaggregator

import com.google.firebase.firestore.PropertyName

data class Instrument(
    val Name: String = "",
    val Price: Long = 0,
    val Shop: String = "",
    val Link : String = "",

    @get:PropertyName("Image URL")
    @set:PropertyName("Image URL")
    var ImageUrl: String = "",

    var isFavorite: Boolean =false
)

