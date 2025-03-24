package com.example.musicalinstrumentsaggregator

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson

object FavoritesManager {
    private val favorites = mutableListOf<Instrument>()
    private lateinit var prefs: SharedPreferences

    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorites_list"

    // Call this once (e.g. in Application.onCreate or MainActivity.onCreate)
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFavoritesFromPrefs()
    }

    fun addFavorite(instrument: Instrument) {
        // Ensure it's not already in the list
        if (!favorites.contains(instrument)) {
            instrument.isFavorite = true
            favorites.add(instrument)
            saveFavoritesToPrefs()
        }
    }

    fun removeFavorite(instrument: Instrument) {
        favorites.remove(instrument)
        instrument.isFavorite = false
        saveFavoritesToPrefs()
    }

    fun getFavorites(): List<Instrument> {
        return favorites
    }

    fun isFavorite(instrument: Instrument): Boolean {
        return favorites.contains(instrument)
    }

    private fun saveFavoritesToPrefs() {
        val gson = Gson()
        val json = gson.toJson(favorites)
        prefs.edit().putString(KEY_FAVORITES, json).apply()
    }

    private fun loadFavoritesFromPrefs() {
        val json = prefs.getString(KEY_FAVORITES, null) ?: return
        val type = object : TypeToken<List<Instrument>>() {}.type
        val loadedList: List<Instrument> = Gson().fromJson(json, type)
        favorites.clear()
        favorites.addAll(loadedList)
    }
}
