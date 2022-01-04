package com.raywenderlich.cinematic.util

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.cinematic.MoviesRecyclerAdapter
import com.raywenderlich.cinematic.data.repository.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyItemTouchHelperCallback(
    private val moviesRepository: MoviesRepository,
    private val lifecycleOwner: LifecycleOwner,
    @MainThread private val onItemChanged: (Boolean) -> Unit = {}
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragDirectionFlags =
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeDirectionFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragDirectionFlags, swipeDirectionFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val adapter = recyclerView.adapter as? MoviesRecyclerAdapter
        adapter?.onItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return adapter != null
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val movieViewHolder = viewHolder as? MoviesRecyclerAdapter.MoviesViewHolder
        val movie = movieViewHolder?.movie
        movie?.let { mv ->
            val movieId = mv.id
            lifecycleOwner.lifecycleScope.launch {
                if (direction == ItemTouchHelper.RIGHT) {
                    moviesRepository.setFavorite(movieId)
                    withContext(Dispatchers.Main) {
                        onItemChanged(true)
                    }
                } else if (direction == ItemTouchHelper.LEFT) {
                    moviesRepository.removeFavorite(movieId)
                    withContext(Dispatchers.Main) {
                        onItemChanged(false)
                    }
                }
            }
        }
        viewHolder.bindingAdapter?.notifyItemChanged(viewHolder.bindingAdapterPosition)
    }

}