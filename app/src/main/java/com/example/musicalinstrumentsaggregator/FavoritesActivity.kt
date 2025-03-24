package com.example.musicalinstrumentsaggregator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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

        // 1. Drawer + Navigation setup
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

        // 2. Populate side menu items (Home + categories)
        val allCategories = InstrumentCategoriesData.getIconList()
        val menu = navView.menu
        menu.add("Home")
        for (category in allCategories) {
            menu.add(category.title)
        }

        // 3. Navigation item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            val selectedTitle = menuItem.title.toString()
            Log.d("NAV_DEBUG", "User tapped: $selectedTitle")

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



        // 4. RecyclerView setup
        val recyclerView = findViewById<RecyclerView>(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 5. Get the current favorites from the manager
        val favoritesList = FavoritesManager.getFavorites().toMutableList()
        val noFavoritesTextView = findViewById<TextView>(R.id.noFavoritesTextView)

        if (favoritesList.isEmpty()) {
            recyclerView.visibility = View.GONE
            noFavoritesTextView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noFavoritesTextView.visibility = View.GONE
        }
        // 6. Use FavoritesAdapter with a callback if the list becomes empty
        recyclerView.adapter = FavoritesAdapter(favoritesList) {
            // Called if the last item is removed => favoritesList.isEmpty()
//            Toast.makeText(this, "The list of favorites is empty", Toast.LENGTH_SHORT).show()

            // Hide the RecyclerView
            recyclerView.visibility = View.GONE
            // Show the "No favorites" text
            noFavoritesTextView.visibility = View.VISIBLE
        }
    }
}
