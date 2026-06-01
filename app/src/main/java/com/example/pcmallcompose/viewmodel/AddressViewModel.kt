package com.example.pcmallcompose.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pcmallcompose.core.network.service.AddressService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressService: AddressService
) : ViewModel() {
    companion object {
        const val TAG = "AddressViewModel"
    }
}