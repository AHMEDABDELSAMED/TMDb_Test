package com.pleac.tmdb_test.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pleac.agc.data.data.Post
import com.pleac.agc.data.data.toFavoriteEntity
import com.pleac.tmdb_test.R
import com.pleac.tmdb_test.domain.model.FavoriteEntity
import com.pleac.tmdb_test.databinding.ItemPostBinding

class PostAdapter (
    private val isFavorite: (Int) -> Boolean,
    private val onFavoriteClick: (FavoriteEntity) -> Unit,
    private val onItemClick: (Post) -> Unit
): PagingDataAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                binding.root.setOnClickListener {
                    onItemClick(post)
                }
                Title.text = post.title

                val favIcon = if (isFavorite(post.id)) {
                    R.drawable.fav
                } else {
                    R.drawable.unfav
                }

                iconFavorite.setImageResource(favIcon)

                iconFavorite.setOnClickListener {
                    onFavoriteClick(post.toFavoriteEntity())
                }

                Glide.with(imagePoster.context)
                    .load("https://image.tmdb.org/t/p/w500${post.posterPath}")
                    .into(imagePoster)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        post?.let { holder.bind(it) }
    }
}
