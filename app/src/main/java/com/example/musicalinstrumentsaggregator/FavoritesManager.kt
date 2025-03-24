package com.example.musicalinstrumentsaggregator

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A singleton object that manages the user's favorite instruments.
 * It keeps them in memory and persists them to SharedPreferences as JSON.
 *
 * Call [init] once (e.g., in your MainActivity or Application class) to load any existing favorites.
 */
object FavoritesManager {

    // In-memory list of favorite instruments
    private val favorites = mutableListOf<Instrument>()

    // SharedPreferences for persisting favorites
    private lateinit var prefs: SharedPreferences

    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorites_list"

    /**
     * Must be called exactly once at app startup to initialize [prefs] and load existing favorites.
     *
     * @param context the application or activity context used to access SharedPreferences
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFavoritesFromPrefs()
    }

    /**
     * Adds the given [instrument] to the favorites list if it's not already present.
     * Also sets [Instrument.isFavorite] to true and saves to SharedPreferences.
     *
     * @param instrument the instrument to mark as favorite
     */
    fun addFavorite(instrument: Instrument) {
        if (!favorites.contains(instrument)) {
            instrument.isFavorite = true
            favorites.add(instrument)
            saveFavoritesToPrefs()
        }
    }

    /**
     * Removes the given [instrument] from the favorites list, sets [Instrument.isFavorite] to false,
     * and saves the updated list to SharedPreferences.
     *
     * @param instrument the instrument to remove from favorites
     */
    fun removeFavorite(instrument: Instrument) {
        favorites.remove(instrument)
        instrument.isFavorite = false
        saveFavoritesToPrefs()
    }

    /**
     * @return a **read-only** list of all favorite instruments currently in memory
     */
    fun getFavorites(): List<Instrument> {
        return favorites
    }

    /**
     * Checks if the given [instrument] is currently in the favorites list.
     *
     * @param instrument the instrument to check
     * @return true if [instrument] is favorited, false otherwise
     */
    fun isFavorite(instrument: Instrument): Boolean {
        return favorites.contains(instrument)
    }

    /**
     * Saves the current [favorites] list to SharedPreferences as a JSON string.
     */
    private fun saveFavoritesToPrefs() {
        val json = Gson().toJson(favorites)
        prefs.edit().putString(KEY_FAVORITES, json).apply()
    }

    /**
     * Loads the favorites list from SharedPreferences if available,
     * and populates the in-memory [favorites] list.
     */
    private fun loadFavoritesFromPrefs() {
        val json = prefs.getString(KEY_FAVORITES, null) ?: return
        val type = object : TypeToken<List<Instrument>>() {}.type
        val loadedList: List<Instrument> = Gson().fromJson(json, type)

        favorites.clear()
        favorites.addAll(loadedList)
    }
}
