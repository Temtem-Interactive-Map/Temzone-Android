package com.temtem.interactive.map.temzone.presentation.map.search

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.temtem.interactive.map.temzone.databinding.SearchLoaderItemBinding

class LoaderViewHolder(
    private val viewBinding: SearchLoaderItemBinding,
    private val callback: () -> Unit,
) : RecyclerView.ViewHolder(viewBinding.root) {

    init {
        viewBinding.retryButton.setOnClickListener {
            callback()
        }
    }

    fun bind(loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                viewBinding.progressBar.visibility = View.VISIBLE
                viewBinding.errorTextView.visibility = View.GONE
                viewBinding.retryButton.visibility = View.GONE
            }

            is LoadState.Error -> {
                viewBinding.progressBar.visibility = View.GONE
                viewBinding.errorTextView.visibility = View.VISIBLE
                viewBinding.errorTextView.text = loadState.error.message
                viewBinding.retryButton.visibility = View.VISIBLE
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
