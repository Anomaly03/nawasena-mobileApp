package com.example.nawasena.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nawasena.data.model.Destination
import com.example.nawasena.data.model.Review
import com.example.nawasena.data.model.User
import com.example.nawasena.data.repository.DestinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: DestinationRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<Destination?>(null)
    val destination: StateFlow<Destination?> = _destination

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadDestination(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Load Data Destinasi
            repository.getDestinationById(id).onSuccess { data ->
                _destination.value = data
            }
            // Load Review
            repository.getReviews(id).onSuccess { data ->
                _reviews.value = data
            }
            _isLoading.value = false
        }
    }

    fun submitReview(destId: String, user: User?, rating: Int, comment: String) {
        // Asumsi user adalah object FirebaseUser, ambil nama dan UID
        // Sesuaikan dengan tipe data user kamu di AuthViewModel
        if (user == null || rating == 0) return
        val uid = user.uid
        val name = user.name

        viewModelScope.launch {
            val newReview = Review(
                userId = uid,
                userName = name,
                rating = rating,
                comment = comment
            )
            repository.addReview(destId, newReview).onSuccess {
                // Refresh data setelah submit
                loadDestination(destId)
            }
        }
    }
}