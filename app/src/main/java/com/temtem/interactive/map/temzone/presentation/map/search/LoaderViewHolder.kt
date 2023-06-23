package com.temtem.interactive.map.temzone.presentation.map.search

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.temtem.interactive.map.temzone.databinding.SearchLoaderItemBinding

class LoaderViewHolder(
    private val viewBinding: SearchLoaderItemBinding,
) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bind(loadState: LoadState) {
        viewBinding.progressBar.isVisible = loadState is LoadState.Loading
    }
}
