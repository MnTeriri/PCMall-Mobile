package com.example.pcmallcompose.core.data.repository

import com.example.pcmallcompose.core.data.apiCall
import com.example.pcmallcompose.core.model.Address
import com.example.pcmallcompose.core.model.dto.AddressDTO
import com.example.pcmallcompose.core.network.service.AddressService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AddressRepository @Inject constructor(
    private val service: AddressService
) {
    suspend fun searchAddressList(uid: String): Result<List<Address>> = apiCall {
        service.searchAddressList(uid).data ?: emptyList()
    }

    suspend fun searchDefaultAddress(uid: String): Result<Address?> = apiCall {
        service.searchDefaultAddress(uid).data
    }

    suspend fun addAddress(address: AddressDTO): Result<Unit> = apiCall {
        service.addAddress(address)
    }

    suspend fun updateAddress(address: AddressDTO): Result<Unit> = apiCall {
        service.updateAddress(address)
    }

    suspend fun deleteAddress(id: Int): Result<Unit> = apiCall {
        service.deleteAddress(id)
    }
}