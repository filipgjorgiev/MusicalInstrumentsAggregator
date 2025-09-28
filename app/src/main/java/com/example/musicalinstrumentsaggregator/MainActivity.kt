package com.example.musicalinstrumentsaggregator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp

/**
 * The main entry point of the app. Displays a grid of category icons,
 * and includes a navigation drawer with "Favorites" and other categories.
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    // Drawer + toggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize Firebase + FavoritesManager
        initializeFirebase()
        FavoritesManager.init(this)

        // 2. Setup Toolbar & Drawer
        setupToolbarAndDrawer()

        // 3. Setup the RecyclerView (grid of icons)
        setupRecyclerView()

        // 4. Setup the NavigationView menu items + handle clicks
        setupNavigationMenu()
    }

    /**
     * Initializes Firebase
     */
    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this)

    }

    /**
     * Finds and sets up the toolbar as the action bar, creates the drawer toggle, and syncs it.
     */
    private fun setupToolbarAndDrawer() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Musical Instruments"

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    /**
     * Sets up the main RecyclerView with a 2-column grid of category icons.
     * Uses [IconAdapter] to handle item clicks that open CategoryDetailActivity.
     */
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Show items in a 2-column grid
        recyclerView.layoutManager = GridLayoutManager(this, 2)


        val iconList = InstrumentCategoriesData.getIconList()

        // Set the adapter
        recyclerView.adapter = IconAdapter(iconList)
    }

    /**
     * Populates the NavigationView menu with "Favorites" + categories,
     * and handles clicks for each item.
     */
    private fun setupNavigationMenu() {
        val navView = findViewById<NavigationView>(R.id.navigationView)
        val iconList = InstrumentCategoriesData.getIconList()


        navView.menu.add("Favorites")

        // Add each category to the menu
        for (iconItem in iconList) {
            navView.menu.add(iconItem.title)
        }

        // Handle clicks in the side menu
        navView.setNavigationItemSelectedListener { menuItem ->
            val selectedTitle = menuItem.title.toString()
            Log.d(TAG, "User tapped: $selectedTitle")

            if (selectedTitle == "Favorites") {
                // Go to FavoritesActivity
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
            } else {
                // All other titles are categories
                val intent = Intent(this, CategoryDetailActivity::class.java).apply {
                    putExtra("categoryName", selectedTitle)
                }
                startActivity(intent)
            }

            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
