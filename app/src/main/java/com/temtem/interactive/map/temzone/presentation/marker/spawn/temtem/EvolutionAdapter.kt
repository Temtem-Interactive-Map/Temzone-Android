package com.temtem.interactive.map.temzone.presentation.marker.spawn.temtem

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.temtem.interactive.map.temzone.databinding.EvolutionItemBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Evolution

class EvolutionAdapter(
    private val context: Context,
    private val evolutions: List<Evolution>,
) : RecyclerView.Adapter<EvolutionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvolutionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = EvolutionItemBinding.inflate(inflater, parent, false)

        return EvolutionViewHolder(context, viewBinding)
    }

    override fun getItemCount(): Int = evolutions.size

    override fun onBindViewHolder(holder: EvolutionViewHolder, position: Int) {
        val evolution = evolutions[position]

        holder.bind(evolution)
    }
}
