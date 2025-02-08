package com.example.pcmallcompose.viewmodel.state

sealed class UiState {
    data class Success(val message: String) : UiState()
    data class Error(val message: String = "", val code: Int? = null) : UiState()
    data object Loading : UiState()
    data object Idle : UiState()
}