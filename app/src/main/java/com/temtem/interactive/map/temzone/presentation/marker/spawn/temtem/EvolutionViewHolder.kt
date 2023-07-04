package com.temtem.interactive.map.temzone.presentation.marker.spawn.temtem

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.temtem.interactive.map.temzone.core.di.GlideApp
import com.temtem.interactive.map.temzone.databinding.EvolutionItemBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Evolution

class EvolutionViewHolder(
    private val context: Context,
    private val viewBinding: EvolutionItemBinding,
) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bind(evolution: Evolution) {
        viewBinding.nameTextView.text = evolution.name.split(" ").first()
        viewBinding.primaryTraitTextView.text = evolution.traits.first()
        viewBinding.secondaryTraitTextView.text = evolution.traits.last()
        viewBinding.conditionTextView.text = evolution.condition
        GlideApp.with(context)
            .load(evolution.imageUrl)
            .into(viewBinding.temtemImageView)
    }
}
