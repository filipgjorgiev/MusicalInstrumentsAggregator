package com.example.musicalinstrumentsaggregator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.text.NumberFormat
import java.util.Locale

/**
 * An adapter for displaying a list of favorited instruments in the Favorites screen.
 * Tapping the heart icon removes the item from both the global FavoritesManager and this list.
 *
 * @param favoritesList the mutable list of favorited instruments
 * @param onEmptyCallback a lambda called when the last item is removed from the list
 */
class FavoritesAdapter(
    private val favoritesList: MutableList<Instrument>,
    private val onEmptyCallback: () -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    /**
     * Holds references to the views for each item (instrument).
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.instrumentImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.instrumentNameTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.instrumentPriceTextView)
        val shopTextView: TextView = itemView.findViewById(R.id.instrumentShopTextView)
        val favouriteIcon: ImageView = itemView.findViewById(R.id.favouriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_instrument_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = favoritesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instrument = favoritesList[position]

        // Set instrument name
        holder.nameTextView.text = instrument.Name
        // Clicking the name opens a link (if available)
        holder.nameTextView.setOnClickListener {
            openInstrumentLink(holder.itemView.context, instrument.Link)
        }

        // Format the price with commas + "MKD"
        val priceFormatted = formatPrice(instrument.Price)
        holder.priceTextView.text = priceFormatted

        // Show the shop name
        holder.shopTextView.text = instrument.Shop

        // Load the instrument image (if any) using Coil
        holder.imageView.load(instrument.ImageUrl)
        holder.imageView.setOnClickListener {
            openInstrumentLink(holder.itemView.context, instrument.Link)
        }

        // This is Favorites, so show a filled heart icon
        holder.favouriteIcon.setImageResource(R.drawable.ic_favorite_filled)

        // When user taps the heart again, remove from favorites
        holder.favouriteIcon.setOnClickListener {
            removeFavoriteItem(holder, position, instrument)
        }
    }

    /**
     * Removes the item from both FavoritesManager and the local list, then calls onEmptyCallback if list is empty.
     */
    private fun removeFavoriteItem(holder: ViewHolder, position: Int, instrument: Instrument) {
        // 1. Remove from global manager
        FavoritesManager.removeFavorite(instrument)

        // 2. Remove from this adapter's data list
        favoritesList.removeAt(holder.adapterPosition)
        notifyItemRemoved(holder.adapterPosition)

        // 3. If the list is now empty, invoke our callback
        if (favoritesList.isEmpty()) {
            onEmptyCallback()
        }
    }

    /**
     * Opens the given link in a browser if not blank, otherwise shows a toast.
     */
    private fun openInstrumentLink(context: Context, link: String) {
        if (link.isNotBlank()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "No link available", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Formats the instrument price with commas and adds "MKD" suffix.
     */
    private fun formatPrice(price: Long): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        val priceWithCommas = numberFormat.format(price)
        return "$priceWithCommas MKD"
    }
}
