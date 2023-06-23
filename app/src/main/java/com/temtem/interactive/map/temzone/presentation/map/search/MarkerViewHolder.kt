package com.temtem.interactive.map.temzone.presentation.map.search

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.temtem.interactive.map.temzone.core.extension.getDrawable
import com.temtem.interactive.map.temzone.databinding.SearchMarkerItemBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker

class MarkerViewHolder(
    private val context: Context,
    private val viewBinding: SearchMarkerItemBinding,
    private val listener: (Marker) -> Unit,
) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bind(marker: Marker) {
        viewBinding.markerImageView.setImageDrawable(marker.getDrawable(context))
        viewBinding.titleTextView.text = marker.title.split(" ").first()
        viewBinding.subtitleTextView.text = marker.subtitle

        viewBinding.root.setOnClickListener {
            listener(marker)
        }
    }
}
