package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.repository.AddressRepository
import com.example.pcmallcompose.core.model.dto.AddressDTO
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddressEditUiState(
    val isEditSuccess: Boolean = false,
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class AddressEditViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {
    companion object {
        private const val TAG = "AddressEditViewModel"
    }

    private val _uiState = MutableStateFlow(AddressEditUiState())
    val uiState: StateFlow<AddressEditUiState> = _uiState.asStateFlow()

    fun addAddress(address: AddressDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isEditSuccess = false) }
            addressRepository.addAddress(address)
                .onSuccess { _uiState.update { it.copy(isEditSuccess = true) } }
                .onFailure { handleError(it) }
        }
    }

    fun updateAddress(address: AddressDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isEditSuccess = false) }
            addressRepository.updateAddress(address)
                .onSuccess { _uiState.update { it.copy(isEditSuccess = true) } }
                .onFailure { handleError(it) }
        }
    }

    private fun handleError(e: Throwable) {
        Log.e(TAG, e.toString(), e)
        val msg = if (e is ApiException) {
            Log.w(TAG, e.toString(), e)
            e.message
        } else {
            Log.e(TAG, e.toString(), e)
            e.message.orEmpty()
        }
        _uiState.update { it.copy(errorMessage = ErrorMessage.Toast(msg)) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}