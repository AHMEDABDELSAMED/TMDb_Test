package com.pleac.tmdb_test.presentation.Fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pleac.agc.data.data.Post
import com.pleac.tmdb_test.R
import com.pleac.tmdb_test.domain.model.UiState
import com.pleac.tmdb_test.presentation.adapter.PostAdapter
import com.pleac.tmdb_test.presentation.Viewmodels.PostViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: PostViewModel by viewModel()
    lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var isGrid = false
    private lateinit var toggleLayoutButton: View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        toggleLayoutButton = view.findViewById(R.id.btnToggleLayout)
        setupAdapter()
        recyclerView.adapter = postAdapter
        updateLayoutManager()
        observeData()
        toggleLayoutButton.setOnClickListener {
            isGrid = !isGrid
            updateLayoutManager()
        }
        viewModel.getPosts()


    }

    private fun updateLayoutManager() {
        recyclerView.layoutManager = if (isGrid) {
            toggleLayoutButton.setBackgroundResource(R.drawable.vertical)
            androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)
        } else {
            toggleLayoutButton.setBackgroundResource(R.drawable.grid)
            LinearLayoutManager(requireContext())
        }
    }
    private fun setupAdapter() {
        postAdapter = PostAdapter(
            isFavorite = { movieId -> viewModel.favoriteMovies.value.any { it.id == movieId } },
            onFavoriteClick = { post -> viewModel.toggleFavorite(post) },
            onItemClick = { post -> navigateToDetails(post) }
        )

        recyclerView.adapter = postAdapter
    }
    private fun navigateToDetails(post: Post) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(post.id)
        findNavController().navigate(action)
    }
    private fun observeData() {

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
                    is UiState.Success -> {
                        postAdapter.submitData(state.data)
                    }
                    is UiState.NoInternet -> Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                    is UiState.Empty -> Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                    is UiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
       /*
        lifecycleScope.launch {
            viewModel.posts.collectLatest { pagingData ->
                postAdapter.submitData(pagingData)
            }
        }
        */

        lifecycleScope.launch {
            viewModel.favoriteMovies.collect {
               postAdapter.notifyDataSetChanged()
            }
        }
    }

}