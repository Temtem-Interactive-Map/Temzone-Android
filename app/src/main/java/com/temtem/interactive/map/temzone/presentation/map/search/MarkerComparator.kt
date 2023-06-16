package com.temtem.interactive.map.temzone.presentation.map.search

import androidx.recyclerview.widget.DiffUtil
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker

object MarkerComparator : DiffUtil.ItemCallback<Marker>() {

    override fun areItemsTheSame(oldItem: Marker, newItem: Marker): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Marker, newItem: Marker): Boolean {
        return oldItem == newItem
    }
}
