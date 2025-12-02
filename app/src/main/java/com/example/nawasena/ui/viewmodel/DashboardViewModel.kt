package com.example.nawasena.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nawasena.data.model.Destination
import com.example.nawasena.data.repository.DestinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DestinationRepository
) : ViewModel() {

    // 1. Data Mentah dari Database (Master Data)
    private val _allDestinations = MutableStateFlow<List<Destination>>(emptyList())

    // 2. Data yang Ditampilkan di UI (Bisa berubah kalau di-search)
    private val _uiDestinations = MutableStateFlow<List<Destination>>(emptyList())
    val uiDestinations: StateFlow<List<Destination>> = _uiDestinations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchDestinations()
    }

    // Ambil data dari Repository saat ViewModel dibuat
    private fun fetchDestinations() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAllDestinations()

            result.onSuccess { data ->
                _allDestinations.value = data
                _uiDestinations.value = data // Awalnya tampilkan semua
            }
            // Handle error jika perlu (misal tampilkan snackbar)

            _isLoading.value = false
        }
    }

    // Fungsi SEARCH PINTAR (Nama + Lokasi + Keywords)
    fun search(query: String) {
        val masterData = _allDestinations.value

        if (query.isBlank()) {
            _uiDestinations.value = masterData
        } else {
            val lowerQuery = query.lowercase()
            _uiDestinations.value = masterData.filter { place ->
                place.name.lowercase().contains(lowerQuery) ||
                        place.location.lowercase().contains(lowerQuery) ||
                        place.keywords.any { k -> k.lowercase().contains(lowerQuery) }
            }
        }
    }
}