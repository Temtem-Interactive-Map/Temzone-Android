package com.temtem.interactive.map.temzone.presentation.map.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.temtem.interactive.map.temzone.databinding.SearchLoaderItemBinding

class LoaderAdapter(
    private val adapter: MarkerAdapter,
) : LoadStateAdapter<LoaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = SearchLoaderItemBinding.inflate(inflater, parent, false)

        return LoaderViewHolder(viewBinding) {
            adapter.retry()
        }
    }

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}
