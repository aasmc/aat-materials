package com.raywenderlich.cinematic.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.cinematic.MoviesAdapter
import com.raywenderlich.cinematic.MoviesRecyclerAdapter
import com.raywenderlich.cinematic.data.repository.MoviesRepository
import kotlinx.coroutines.launch

class MyItemTouchHelperCallback(
  private val moviesRepository: MoviesRepository,
  private val lifecycleOwner: LifecycleOwner,
  private val onItemSwiped: (direction: Int) -> Unit
) : ItemTouchHelper.Callback() {

  override fun getMovementFlags(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder
  ): Int {
    // adding all directions for dragging
    val dragDirectionFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    val swipeDirectionFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

    return makeMovementFlags(dragDirectionFlags, swipeDirectionFlags)
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    val movieViewHolder = viewHolder as? MoviesAdapter.MoviesViewHolder
    val movie =
      movieViewHolder?.movie ?: (viewHolder as? MoviesRecyclerAdapter.MoviesViewHolder)?.movie

    if (movie != null) {
      val movieId = movie.id

      lifecycleOwner.lifecycleScope.launch {
        if (direction == ItemTouchHelper.RIGHT) {
          moviesRepository.setFavourite(movieId)
        } else if (direction == ItemTouchHelper.LEFT) {
          moviesRepository.removeFavourite(movieId)
        }
      }
    }

    // notify the Fragment/Activity
    onItemSwiped(direction)
    viewHolder.bindingAdapter?.notifyItemChanged(viewHolder.bindingAdapterPosition)
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
}