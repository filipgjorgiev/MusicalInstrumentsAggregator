package com.example.musicalinstrumentsaggregator

import com.google.firebase.firestore.PropertyName

data class Instrument(
    val id: String = "",
    val Name: String = "",
    val Price: Long = 0,
    val Shop: String = "",
    val Link : String = "",

    @get:PropertyName("Image URL")
    @set:PropertyName("Image URL")
    var ImageUrl: String = "",

    var isFavorite: Boolean =false )
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Instrument) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

