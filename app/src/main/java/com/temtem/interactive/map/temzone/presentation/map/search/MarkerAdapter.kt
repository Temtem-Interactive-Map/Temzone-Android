package com.temtem.interactive.map.temzone.presentation.map.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.temtem.interactive.map.temzone.databinding.SearchMarkerItemBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker

class MarkerAdapter(
    diffCallback: DiffUtil.ItemCallback<Marker>,
    private val context: Context,
    private val listener: (Marker) -> Unit,
) : PagingDataAdapter<Marker, MarkerViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = SearchMarkerItemBinding.inflate(inflater, parent, false)

        return MarkerViewHolder(context, viewBinding, listener)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null) {
            holder.bind(item)
        }
    }
}
