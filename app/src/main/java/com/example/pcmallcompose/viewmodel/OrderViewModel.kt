package com.example.pcmallcompose.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pcmallcompose.application.UserSession
import com.example.pcmallcompose.core.database.PCMallDatabase
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OrderUiState(
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val database: PCMallDatabase,
    private val userSession: UserSession
) : ViewModel() {
    companion object {
        const val TAG = "OrderViewModel"
    }

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
}