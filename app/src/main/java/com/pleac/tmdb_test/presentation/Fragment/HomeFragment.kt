package com.pleac.tmdb_test.presentation.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.pleac.agc.data.data.Post
import com.pleac.tmdb_test.R
import com.pleac.tmdb_test.databinding.FragmentHomeBinding
import com.pleac.tmdb_test.domain.model.UiState
import com.pleac.tmdb_test.presentation.adapter.PostAdapter
import com.pleac.tmdb_test.presentation.Viewmodels.PostViewModel
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModel()
    private lateinit var postAdapter: PostAdapter
    private var isGrid = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        _binding?.recyclerView?.adapter = postAdapter
        updateLayoutManager()
        observeData()
        _binding?.btnToggleLayout?.setOnClickListener {
            isGrid = !isGrid
            updateLayoutManager()
        }
        viewModel.getPosts()


    }

    private fun updateLayoutManager() {
        _binding?.recyclerView?.layoutManager = if (isGrid) {
            _binding?.btnToggleLayout?.setBackgroundResource(R.drawable.vertical)
            androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)
        } else {
            _binding?.btnToggleLayout?.setBackgroundResource(R.drawable.grid)
            LinearLayoutManager(requireContext())
        }
    }
    private fun setupAdapter() {
        postAdapter = PostAdapter(
            onFavoriteClick = { post -> viewModel.toggleFavorite(post) },
            onItemClick = { post -> navigateToDetails(post) }
        )

        _binding?.recyclerView?.adapter = postAdapter
    }
    private fun navigateToDetails(post: Post) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(post.id)
        findNavController().navigate(action)
    }
    private fun observeData() {

        lifecycleScope.launch {
            postAdapter.loadStateFlow.collect { loadStates ->
                val refresh = loadStates.refresh
                when (refresh) {
                    is LoadState.Error -> {
                        val error = (refresh).error
                        if (error is IOException) {
                            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is LoadState.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }

                    is LoadState.NotLoading -> {
                        Toast.makeText(requireContext(), "No loading", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
                        is UiState.Success -> postAdapter.submitData(state.data)
                        is UiState.NoInternet -> Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                        is UiState.Empty -> Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                        is UiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        lifecycleScope.launch {
            viewModel.updatedFavoriteId.collect { id ->
                postAdapter.updateFavoriteItemById(id)
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteMovies.collect { favorites ->
                    val favIds = favorites.map { it.id }.toSet()
                    postAdapter.updateFavorites(favIds)
                }
            }
        }
    }

}