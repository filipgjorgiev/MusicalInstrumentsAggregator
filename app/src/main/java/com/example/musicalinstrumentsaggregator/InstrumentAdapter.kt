package com.example.musicalinstrumentsaggregator

import android.annotation.SuppressLint
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
 * An adapter for displaying a list of [Instrument] items.
 * Allows toggling an instrument as favorite, opening a link, etc.
 *
 * @property instrumentList a mutable list of [Instrument] items to display
 */
class InstrumentAdapter(
    private val instrumentList: MutableList<Instrument>
) : RecyclerView.Adapter<InstrumentAdapter.ViewHolder>() {

    /**
     * Holds references to the views for each item row (instrument).
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

    override fun getItemCount(): Int = instrumentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instrument = instrumentList[position]

        // Bind name, with a click opening the instrument link if available
        holder.nameTextView.text = instrument.Name
        holder.nameTextView.setOnClickListener {
            openInstrumentLink(holder.itemView.context, instrument.Link)
        }

        // Format and bind the price
        val priceFormatted = formatPrice(instrument.Price)
        holder.priceTextView.text = priceFormatted

        // Show the shop name
        holder.shopTextView.text = instrument.Shop

        // Load the image via Coil
        holder.imageView.load(instrument.ImageUrl)
        holder.imageView.setOnClickListener {
            openInstrumentLink(holder.itemView.context, instrument.Link)
        }

        // Show the correct heart icon (filled or outline) based on FavoritesManager
        if (FavoritesManager.isFavorite(instrument)) {
            holder.favouriteIcon.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            holder.favouriteIcon.setImageResource(R.drawable.ic_favorite_border)
        }

        // Toggle favorites on icon click
        holder.favouriteIcon.setOnClickListener {
            toggleFavorite(holder, instrument)
        }
    }

    /**
     * Replaces the current [instrumentList] data with [newList], then refreshes the UI.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Instrument>) {
        instrumentList.clear()
        instrumentList.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * Opens [link] in a browser if it's not blank, otherwise shows a toast.
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
     * Formats [price] with commas and adds "MKD" suffix.
     */
    private fun formatPrice(price: Long): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        val priceWithCommas = numberFormat.format(price)
        return "$priceWithCommas MKD"
    }

    /**
     * Toggles an [instrument]'s favorite state in [FavoritesManager],
     * then updates the icon accordingly in [holder].
     */
    private fun toggleFavorite(holder: ViewHolder, instrument: Instrument) {
        if (FavoritesManager.isFavorite(instrument)) {
            FavoritesManager.removeFavorite(instrument)
            holder.favouriteIcon.setImageResource(R.drawable.ic_favorite_border)
        } else {
            FavoritesManager.addFavorite(instrument)
            holder.favouriteIcon.setImageResource(R.drawable.ic_favorite_filled)
        }
    }
}
