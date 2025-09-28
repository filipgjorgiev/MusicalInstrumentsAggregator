package com.example.musicalinstrumentsaggregator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class FavoritesActivity : AppCompatActivity() {

    private lateinit var detailToolbar: Toolbar
    private lateinit var detailDrawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        setupToolbarAndDrawer()


        setupNavigationItems()


        setupRecyclerViewAndEmptyState()
    }

    /**
     * Set up the toolbar as the action bar and create the drawer toggle (hamburger icon).
     */
    private fun setupToolbarAndDrawer() {
        detailDrawerLayout = findViewById(R.id.detailDrawerLayout)
        navView = findViewById(R.id.navigationDetailView)
        detailToolbar = findViewById(R.id.detailToolbar)

        setSupportActionBar(detailToolbar)
        supportActionBar?.title = "Favorites"

        toggle = ActionBarDrawerToggle(
            this,
            detailDrawerLayout,
            detailToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        detailDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    /**
     * Populate the side menu with "Home" and all categories.
     * Handle clicks on each menu item.
     */
    private fun setupNavigationItems() {
        val allCategories = InstrumentCategoriesData.getIconList()
        val menu = navView.menu


        menu.add("Home")


        for (category in allCategories) {
            menu.add(category.title)
        }

        // Handle menu item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            val selectedTitle = menuItem.title.toString()

            when (selectedTitle) {
                "Home" -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
                else -> {

                    val intent = Intent(this, CategoryDetailActivity::class.java)
                    intent.putExtra("categoryName", selectedTitle)
                    startActivity(intent)
                }
            }
            detailDrawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    /**
     * Get the current favorites from FavoritesManager.
     * If the list is empty, show a "No favorites" text.
     * Otherwise, show the RecyclerView with FavoritesAdapter.
     */
    private fun setupRecyclerViewAndEmptyState() {
        val recyclerView = findViewById<RecyclerView>(R.id.favoritesRecyclerView)
        val noFavoritesTextView = findViewById<TextView>(R.id.noFavoritesTextView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Current favorites
        val favoritesList = FavoritesManager.getFavorites().toMutableList()

        // Check if empty
        if (favoritesList.isEmpty()) {
            recyclerView.visibility = View.GONE
            noFavoritesTextView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noFavoritesTextView.visibility = View.GONE
        }

        // Set the adapter with a callback when the last item is removed
        recyclerView.adapter = FavoritesAdapter(favoritesList) {
            // Called if the last item is removed => favoritesList.isEmpty()
            recyclerView.visibility = View.GONE
            noFavoritesTextView.visibility = View.VISIBLE
        }
    }
}
