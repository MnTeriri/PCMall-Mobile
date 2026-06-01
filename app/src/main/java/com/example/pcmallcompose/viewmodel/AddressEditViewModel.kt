package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.core.model.dto.AddressDTO
import com.example.pcmallcompose.core.network.service.AddressService
import com.example.pcmallcompose.core.network.utils.RetrofitUtils
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class AddressEditUiState(
    val isEditSuccess: Boolean = false,
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class AddressEditViewModel @Inject constructor(
    private val addressService: AddressService
) : ViewModel() {
    companion object {
        const val TAG = "AddressEditViewModel"
    }

    private val _uiState = MutableStateFlow(AddressEditUiState())
    val uiState: StateFlow<AddressEditUiState> = _uiState.asStateFlow()

    fun addAddress(address: AddressDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isEditSuccess = false) }
            try {
                addressService.addAddress(address)
                _uiState.update { it.copy(isEditSuccess = true) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    fun updateAddress(address: AddressDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isEditSuccess = false) }
            try {
                addressService.updateAddress(address)
                _uiState.update { it.copy(isEditSuccess = true) }
            } catch (e: HttpException) {
                catchHttpException(e)
            } catch (e: Exception) {
                catchException(e)
            }
        }
    }

    private fun catchHttpException(e: HttpException) {
        val response = RetrofitUtils.getErrorMessage(e)
        if (response == null) {
            catchException(e)
            return
        }
        Log.w(TAG, "$e: $response", e)
        _uiState.update { it.copy(errorMessage = ErrorMessage.Toast(response.message)) }
    }

    private fun catchException(e: Exception) {
        Log.e(TAG, e.toString(), e)
        _uiState.update { it.copy(errorMessage = ErrorMessage.Toast("${e.message}")) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}