package com.example.musicalinstrumentsaggregator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var detailDrawerLayout: DrawerLayout
    private lateinit var detailToolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var filterSpinnerPrice: Spinner
    private lateinit var filterSpinnerShop: Spinner

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
        val shopOptions = listOf("None", "Shop A", "Shop B", "Shop C")

        // Create ArrayAdapter for Spinner 1
        val adapter1 = ArrayAdapter(this, R.layout.spinner_item, priceOptions)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinnerPrice.adapter = adapter1

        // Create ArrayAdapter for Spinner 2
        val adapter2 = ArrayAdapter(this, R.layout.spinner_item, shopOptions)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinnerShop.adapter = adapter2




    }
}

