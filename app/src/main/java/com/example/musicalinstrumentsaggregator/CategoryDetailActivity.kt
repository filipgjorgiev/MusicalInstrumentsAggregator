package com.example.musicalinstrumentsaggregator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class CategoryDetailActivity : AppCompatActivity() {

    private val TAG = "CategoryDetail"

    // Drawer / Toolbar
    private lateinit var detailDrawerLayout: DrawerLayout
    private lateinit var detailToolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    // Spinners (for filtering)
    private lateinit var filterSpinnerPrice: Spinner
    private lateinit var filterSpinnerShop: Spinner

    // SearchView
    private lateinit var searchView: SearchView

    // The complete list of instruments for this category
    private var allInstruments: List<Instrument> = emptyList()

    // Adapter for displaying instruments
    private lateinit var adapter: InstrumentAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        // 1. Initialize Views & Toolbar
        detailDrawerLayout = findViewById(R.id.detailDrawerLayout)
        detailToolbar = findViewById(R.id.detailToolbar)
        navView = findViewById(R.id.navigationDetailView)

        // Set the Toolbar as the ActionBar
        setSupportActionBar(detailToolbar)

        // Create & sync the hamburger toggle for the drawer
        toggle = ActionBarDrawerToggle(
            this,
            detailDrawerLayout,
            detailToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        detailDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 2. Retrieve Category Name from Intent
        val categoryName = intent.getStringExtra("categoryName") ?: "Unknown Category"
        supportActionBar?.title = categoryName

        // 3. Populate the Navigation Drawer items (Home, Favorites, and other categories)
        setupNavigationDrawer()

        // 4. Setup Filtering Spinners
        setupSpinners()

        // 5. Setup SearchView
        setupSearchView()

        // 6. Fetch Data from Firestore for this Category
        val db = FirebaseFirestore.getInstance()
        db.collection("/musical_instruments")
            .whereEqualTo("Category", categoryName)
            .get()
            .addOnSuccessListener { documents ->
                val instrumentList = mutableListOf<Instrument>()
                for (document in documents) {
                    // Use Firestore doc ID as the unique instrument id
                    val docId = document.id
                    val instrument = document.toObject(Instrument::class.java)
                        .copy(id = docId)

                    // If it's already in favorites, mark it so the UI shows the correct heart icon
                    if (FavoritesManager.isFavorite(instrument)) {
                        instrument.isFavorite = true
                    }
                    instrumentList.add(instrument)
                }
                // Store them in allInstruments for later filtering
                allInstruments = instrumentList

                // Display them in the RecyclerView
                setupRecyclerView(instrumentList)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching instruments from Firestore", exception)
            }
    }

    /**
     * Populate the navigation drawer with "Home", "Favorites",
     * and the list of all categories from InstrumentCategoriesData.
     */
    private fun setupNavigationDrawer() {
        val menu = navView.menu

        // Add Home and Favorites items
        menu.add("Home")
        menu.add("Favorites")

        // Add other categories from the data
        val allCategories = InstrumentCategoriesData.getIconList()
        for (category in allCategories) {
            menu.add(category.title)
        }

        // Handle clicks on the drawer menu items
        navView.setNavigationItemSelectedListener { menuItem ->
            val selectedTitle = menuItem.title.toString()

            when (selectedTitle) {
                "Home" -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
                "Favorites" -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
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
    }

    /**
     * Setup the two spinners for filtering by price and shop.
     */
    private fun setupSpinners() {
        filterSpinnerPrice = findViewById(R.id.filterSpinnerPrice)
        filterSpinnerShop = findViewById(R.id.filterSpinnerShop)

        // Sample data for the spinners
        val priceOptions = listOf("None", "Low to High", "High to Low")
        val shopOptions = listOf("None", "Do Re Mi", "Artist", "Mimi Muzika")

        // Price spinner
        val priceAdapter = ArrayAdapter(this, R.layout.spinner_item, priceOptions)
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinnerPrice.adapter = priceAdapter

        // Shop spinner
        val shopAdapter = ArrayAdapter(this, R.layout.spinner_item, shopOptions)
        shopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinnerShop.adapter = shopAdapter

        // Listen for changes and apply filters
        filterSpinnerPrice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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

    /**
     * Setup the search view to filter instruments by name as the user types.
     */
    private fun setupSearchView() {
        searchView = findViewById(R.id.searchView)
        searchView.queryHint = "Search by name"
        searchView.isIconified = false // Expand it by default

        // Listen for text changes
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true // Handle filtering in onQueryTextChange
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText ?: ""

                // Filter the allInstruments list by name
                val filteredList = allInstruments.filter { instrument ->
                    instrument.Name.contains(query, ignoreCase = true)
                }

                // Update the RecyclerView
                setupRecyclerView(filteredList)
                return true
            }
        })
    }

    /**
     * Apply filters based on the current spinner selections (price & shop).
     */
    private fun applyFilters() {
        val selectedPriceFilter = filterSpinnerPrice.selectedItem.toString()
        val selectedShopFilter = filterSpinnerShop.selectedItem.toString()


        var filteredList = allInstruments

        // Filter by shop
        if (selectedShopFilter != "None") {
            filteredList = filteredList.filter { it.Shop == selectedShopFilter }
        }

        // Sort by price
        filteredList = when (selectedPriceFilter) {
            "Low to High" -> filteredList.sortedBy { it.Price }
            "High to Low" -> filteredList.sortedByDescending { it.Price }
            else -> filteredList
        }

        // Update the RecyclerView with the filtered list
        setupRecyclerView(filteredList)
    }

    /**
     * Initialize or update the RecyclerView with a given list of instruments.
     * If the adapter isn't created yet, create it. Otherwise, update its data.
     */
    private fun setupRecyclerView(instrumentList: List<Instrument>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewInCategory)

        // If adapter not yet initialized, create it
        if (!::adapter.isInitialized) {
            adapter = InstrumentAdapter(instrumentList.toMutableList())
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        } else {
            // Just update the existing adapter's data
            adapter.updateData(instrumentList)
        }
    }
}
