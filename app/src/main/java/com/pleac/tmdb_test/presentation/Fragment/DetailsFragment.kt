package com.pleac.tmdb_test.presentation.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.pleac.tmdb_test.databinding.FragmentDetailsBinding
import com.pleac.tmdb_test.presentation.Viewmodels.DetailsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: DetailsViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = args.movieId
        viewModel.loadMovieDetails(movieId)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launch {
            viewModel.post.collect { post ->
                post?.let {
                    binding.title.text = it.title
                    binding.description.text = it.overview
                    binding.textDirector.text = "Original Language: ${it.originalLanguage}"
                    binding.textActors.text = "Release Date: ${it.releaseDate}"
                    binding.textPlot.text = "Rating: ${it.voteAverage} (${it.voteCount} votes)"

                    // عرض الجنرا
                    binding.textGenres.text = "Genres: ${it.genreIds}"

                    // تحميل الصورة
                    Glide.with(binding.poster.context)
                        .load("https://image.tmdb.org/t/p/w500${it.posterPath}")
                        .into(binding.poster)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
