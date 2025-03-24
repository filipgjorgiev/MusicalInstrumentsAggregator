package com.example.musicalinstrumentsaggregator

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for displaying a list of icon items (title + image).
 * Tapping an item navigates to [CategoryDetailActivity] with the associated category name.
 *
 * @param items the list of [IconItem] to display
 */
class IconAdapter(
    private val items: List<IconItem>
) : RecyclerView.Adapter<IconAdapter.ViewHolder>() {

    /**
     * Holds references to views for each icon item (image + title).
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemImage)
        val title: TextView = itemView.findViewById(R.id.itemTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_icon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]

        // Bind the icon title and image resource
        holder.title.text = currentItem.title
        holder.image.setImageResource(currentItem.imageRes)

        // When the user taps an item, open CategoryDetailActivity for that category
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CategoryDetailActivity::class.java).apply {
                putExtra("categoryName", currentItem.title)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}

/**
 * Represents a single icon item with a [title] and an [imageRes] resource ID.
 */
data class IconItem(
    val title: String,
    val imageRes: Int
)
