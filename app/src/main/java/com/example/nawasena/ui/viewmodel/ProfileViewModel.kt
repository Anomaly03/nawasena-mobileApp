package com.example.nawasena.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nawasena.data.local.UserProfile
import com.example.nawasena.data.model.User
import com.example.nawasena.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository, // Ganti DAO & Firestore dengan Repository
    private val currentUser: User?
) : ViewModel() {

    private val _profileState = MutableStateFlow<UserProfile?>(null)
    val profileState: StateFlow<UserProfile?> = _profileState

    init {
        currentUser?.let { user ->
            // 1. Observe data lokal
            observeLocalProfile(user.uid)

            // 2. Trigger sync cloud
            syncData(user)
        }
    }

    private fun observeLocalProfile(uid: String) {
        viewModelScope.launch {
            repository.getLocalProfile(uid).collectLatest { profile ->
                _profileState.value = profile
            }
        }
    }

    private fun syncData(user: User) {
        viewModelScope.launch {
            repository.syncProfileFromCloud(user)
        }
    }

    // Fungsi Update dari UI
    fun updateProfile(name: String, username: String, phone: String, birth: String) {
        val uid = currentUser?.uid ?: return
        val email = currentUser.email

        val updatedProfile = UserProfile(
            uid = uid,
            name = name,
            email = email,
            username = username,
            phoneNumber = phone,
            birthDate = birth
        )

        viewModelScope.launch {
            repository.updateProfile(updatedProfile)
        }
    }

    // Fungsi public untuk dipanggil manual dari MainActivity jika perlu
    fun ensureUserInRoom(firebaseUser: User) {
        syncData(firebaseUser)
    }
}

// Update Factory
class ProfileViewModelFactory(
    private val repository: ProfileRepository, // Terima Repository
    private val user: User?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}