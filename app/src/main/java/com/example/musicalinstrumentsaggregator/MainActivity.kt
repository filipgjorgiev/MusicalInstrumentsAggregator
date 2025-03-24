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
import com.example.musicalinstrumentsaggregator.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val TAG = "FirestoreDebug"
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize FavoritesManager once
        FavoritesManager.init(this)

        // Get Firestore instance
//        val db = FirebaseFirestore.getInstance()
//
//        // Check if Firestore is initialized
//        if (db != null) {
//            Log.d(TAG, "Firestore initialized successfully!")
//        } else {
//            Log.e(TAG, "Firestore failed to initialize!")
//            return
//        }
//
//        // Fetch data from Firestore
//        db.collection("/musical_instruments")
//            .get()
//            .addOnSuccessListener { documents ->
//                Log.d(TAG, "Success! Found ${documents.size()} documents.")
//                if (documents.isEmpty) {
//                    Log.d(TAG, "No documents in 'musical_instruments' collection.")
//                } else {
//                    for (document in documents) {
//                        Log.d(TAG, "Document ID: ${document.id}")
//                        Log.d(TAG, "Data: ${document.data}")
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e(TAG, "Firestore Error: ", exception)
//            }



        // 1. Find views
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)

        // 2. Set the Toolbar as the ActionBar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        // 3. Create ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,  // "Open drawer" string
            R.string.navigation_drawer_close  // "Close drawer" string
        )

        // 4. Attach the toggle to the DrawerLayout
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 4. Optional: Setup RecyclerView for the grid
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.layoutManager = GridLayoutManager(this, 2)
//        recyclerView.adapter = MusicalInstrumentAdapter(getItems())
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Show items in a grid of 2 columns
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Prepare your list of icons
        val iconList = InstrumentCategoriesData.getIconList()

        // Set the adapter
        recyclerView.adapter = IconAdapter(iconList)


        val navView = findViewById<NavigationView>(R.id.navigationView)

// 1. Get the menu object from the NavigationView
        val menu = navView.menu

// Add a "Home" item
        menu.add("Favorites")

// Add each category name from iconList as a menu item
        for (iconItem in iconList) {
            menu.add(iconItem.title)
        }

// 2. Handle clicks in the side menu
        navView.setNavigationItemSelectedListener { menuItem ->
            val selectedTitle = menuItem.title.toString()

            if (selectedTitle == "Favorites") {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)

            } else {
                // All other titles are categories
                val intent = Intent(this, CategoryDetailActivity::class.java)
                intent.putExtra("categoryName", selectedTitle)
                startActivity(intent)
            }

            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

// 3. (Optional) Handle menu item clicks
//        navView.setNavigationItemSelectedListener { menuItem ->
//            // Here you can see which item was clicked
//            // For example, compare menuItem.title or use an ID
//            // e.g. "Acoustic Guitars", "Electric Guitars", etc.
//            when (menuItem.title) {
//                "Acoustic Guitars" -> {
//                    // do something
//                }
//                "Electric Guitars" -> {
//                    // do something
//                }
//                // ...
//            }
//
//            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START)
            true
//        }


    }
}
