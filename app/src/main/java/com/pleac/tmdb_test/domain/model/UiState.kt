package com.pleac.tmdb_test.domain.model

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object NoInternet : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}
