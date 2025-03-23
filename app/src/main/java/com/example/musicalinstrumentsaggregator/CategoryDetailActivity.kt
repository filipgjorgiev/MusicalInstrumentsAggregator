package com.example.musicalinstrumentsaggregator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class CategoryDetailActivity : AppCompatActivity() {
    private val TAG = "FirestoreDebug"
    private lateinit var detailDrawerLayout: DrawerLayout
    private lateinit var detailToolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var filterSpinnerPrice: Spinner
    private lateinit var filterSpinnerShop: Spinner

    private lateinit var searchView: SearchView

    private var allInstruments: List<Instrument> = emptyList()

    private lateinit var adapter: InstrumentAdapter


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        // 1. Find Views
        detailDrawerLayout = findViewById(R.id.detailDrawerLayout)
        detailToolbar = findViewById(R.id.detailToolbar)
        navView = findViewById(R.id.navigationDetailView)

        // 2. Setup the toolbar as the action bar
        setSupportActionBar(detailToolbar)

        // 3. Create the drawer toggle (hamburger icon)
        toggle = ActionBarDrawerToggle(
            this,
            detailDrawerLayout,
            detailToolbar,
            R.string.navigation_drawer_open,  // from strings.xml
            R.string.navigation_drawer_close  // from strings.xml
        )
        detailDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 4. Get the category name from the Intent
        val categoryName = intent.getStringExtra("categoryName") ?: "Unknown Category"
        supportActionBar?.title = categoryName

        // 5. (Optional) Setup navigation items in detailNavigationView
        // e.g., add "Home" and categories if you want the same menu:
        // val menu = navView.menu
        // menu.add("Home")
        // menu.add("Another item")
        // ...

        val allCategories=InstrumentCategoriesData.getIconList()

        val menu = navView.menu

        menu.add("Home")

        for (category in allCategories){
            menu.add(category.title)
        }


        // 6. Handle menu item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            val selectedTitle = menuItem.title.toString()

            when (selectedTitle) {
                "Home" -> {
                    // e.g., go back to MainActivity or refresh the main page
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
                else -> {
                    // All other titles are categories
                    val intent = Intent(this, CategoryDetailActivity::class.java)
                    intent.putExtra("categoryName", selectedTitle)
                    startActivity(intent)
                }
            }

            detailDrawerLayout.closeDrawer(GravityCompat.START)
            true
        }



        filterSpinnerPrice = findViewById(R.id.filterSpinnerPrice)
        filterSpinnerShop = findViewById(R.id.filterSpinnerShop)

        // 3. Populate Spinners with sample data
        // Populate them with sample data
        val priceOptions = listOf("None", "Low to High", "High to Low")
        val shopOptions = listOf("None", "Do Re Mi", "Artist", "Mimi Muzika")

        // Create ArrayAdapter for Spinner 1
        val adapter1 = ArrayAdapter(this, R.layout.spinner_item, priceOptions)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinnerPrice.adapter = adapter1

        // Create ArrayAdapter for Spinner 2
        val adapter2 = ArrayAdapter(this, R.layout.spinner_item, shopOptions)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinnerShop.adapter = adapter2

//initializing firestore
        val db = FirebaseFirestore.getInstance()


////        // Check if Firestore is initialized
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

        // 3. Query instruments for this category
        db.collection("/musical_instruments")
            .whereEqualTo("Category", categoryName)
            .get()
            .addOnSuccessListener { documents ->
                // -- Place your code snippet here --
                val instrumentList = mutableListOf<Instrument>()
                for (document in documents) {

                    val instrument = document.toObject(Instrument::class.java)
                    instrumentList.add(instrument)
                }

                allInstruments=instrumentList

                // Now you have a list of Instrument objects
                setupRecyclerView(instrumentList)
            }
            .addOnFailureListener { exception ->
                // Handle error
                Log.e("CategoryDetailActivity", "Error fetching instruments", exception)
            }

        // (Optional) Set up the toolbar title
        supportActionBar?.title = categoryName

        // 1. Find the SearchView
        searchView = findViewById(R.id.searchView)
        searchView.queryHint = "Search by name"
        searchView.isIconified = false // Expand it

        // 2. Set a listener to respond to query changes
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Called when user hits "search" or enters text, then confirms
                return true // We handle it in onQueryTextChange for live filtering
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Called as the user types each character
                val query = newText ?: ""

                // Filter the allInstruments list
                val filteredList = allInstruments.filter { instrument ->
                    instrument.Name.contains(query, ignoreCase = true)
                }

                // Rebind the RecyclerView with the filtered list
                setupRecyclerView(filteredList)

                return true
            }
        })


        filterSpinnerPrice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Re-filter every time a selection changes
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        filterSpinnerShop.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }









    }

    private fun applyFilters() {
        // 1. Get current spinner selections
        val selectedPriceFilter = filterSpinnerPrice.selectedItem.toString() // "None", "Low to High", "High to Low"
        val selectedShopFilter = filterSpinnerShop.selectedItem.toString()   // "None", "Do Re Mi", etc.

        // 2. Start with the full list
        var filteredList = allInstruments

        // 3. Filter by shop if not "None"
        if (selectedShopFilter != "None") {
            filteredList = filteredList.filter { it.Shop == selectedShopFilter }
        }

        // 4. Sort by price if needed
        filteredList = when (selectedPriceFilter) {
            "Low to High" -> filteredList.sortedBy { it.Price }  // ascending
            "High to Low" -> filteredList.sortedByDescending { it.Price }
            else -> filteredList // "None" => no sorting
        }

        // 5. Update the RecyclerView
        setupRecyclerView(filteredList)
    }



    private fun setupRecyclerView(instrumentList: List<Instrument>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewInCategory)
        if (!::adapter.isInitialized) {
            // First time
            adapter = InstrumentAdapter(instrumentList.toMutableList())
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        } else {
            // Just update the existing adapter data
            adapter.updateData(instrumentList)
        }
    }



}

