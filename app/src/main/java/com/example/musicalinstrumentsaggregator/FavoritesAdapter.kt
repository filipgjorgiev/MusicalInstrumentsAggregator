package com.example.musicalinstrumentsaggregator

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

class FavoritesAdapter(
    private val favoritesList: MutableList<Instrument>,
    private val onEmptyCallback: () -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instrument = favoritesList[position]

        // It's Favorites, so presumably already a favorite
        holder.nameTextView.text = instrument.Name

        holder.nameTextView.setOnClickListener {
            val context = holder.itemView.context
            if (instrument.Link.isNotBlank()) {
                // Launch the browser with this link
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instrument.Link))
                context.startActivity(intent)
            } else {
                // Optional: Show a toast if there's no link
                Toast.makeText(context, "No link available", Toast.LENGTH_SHORT).show()
            }
        }

        val numberFormat = NumberFormat.getNumberInstance(Locale.US) // or your preferred Locale
        val priceWithCommas = numberFormat.format(instrument.Price)  // e.g. "8,950"
        val priceFormatted = "$priceWithCommas MKD"
        // Shop
        holder.priceTextView.text=priceFormatted

        holder.shopTextView.text = instrument.Shop

        // Image (Load from imageUrl with a library like Glide or Coil)
        holder.imageView.load(instrument.ImageUrl)

        holder.imageView.setOnClickListener {
            val context = holder.itemView.context
            if (instrument.Link.isNotBlank()) {
                // Launch the browser with this link
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instrument.Link))
                context.startActivity(intent)
            } else {
                // Optional: Show a toast if there's no link
                Toast.makeText(context, "No link available", Toast.LENGTH_SHORT).show()
            }
        }

        holder.favouriteIcon.setImageResource(R.drawable.ic_favorite_filled)

        // When user taps to un-favorite
        holder.favouriteIcon.setOnClickListener {
            // 1. Remove from global manager
            FavoritesManager.removeFavorite(instrument)

            // 2. Remove from adapter's data list
            favoritesList.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

            // 3. If the list is now empty, invoke our callback
            if (favoritesList.isEmpty()) {
                onEmptyCallback()
            }
        }
    }

    override fun getItemCount(): Int = favoritesList.size
}
