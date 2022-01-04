package com.raywenderlich.cinematic.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.cinematic.MoviesAdapter
import com.raywenderlich.cinematic.data.repository.MoviesRepository
import kotlinx.coroutines.launch

class MyItemTouchHelperCallback(
    private val moviesRepository: MoviesRepository,
    private val lifecycleOwner: LifecycleOwner
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragDirectionFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeDirectionFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragDirectionFlags, swipeDirectionFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val movieViewHolder = viewHolder as? MoviesAdapter.MoviesViewHolder
        val movie = movieViewHolder?.movie
        movie?.let { mv ->
            val movieId = mv.id
            lifecycleOwner.lifecycleScope.launch {
                if (direction == ItemTouchHelper.RIGHT) {
                    moviesRepository.setFavorite(movieId)
                } else if (direction == ItemTouchHelper.LEFT) {
                    moviesRepository.removeFavorite(movieId)
                }
            }
        }
        viewHolder.bindingAdapter?.notifyItemChanged(viewHolder.bindingAdapterPosition)
    }

}