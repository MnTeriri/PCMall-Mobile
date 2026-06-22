package com.example.pcmallcompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcmallcompose.core.data.ApiException
import com.example.pcmallcompose.core.data.repository.BrandRepository
import com.example.pcmallcompose.core.data.repository.CategoryRepository
import com.example.pcmallcompose.core.model.Brand
import com.example.pcmallcompose.core.model.Category
import com.example.pcmallcompose.ui.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val brandsMap: Map<Int, List<Brand>> = emptyMap(),
    val errorMessage: ErrorMessage? = null,
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val brandRepository: BrandRepository
) : ViewModel() {
    companion object {
        private const val TAG = "CategoryViewModel"
    }

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    /** 缓存优先：先展示 Room 缓存，再拉远程刷新 */
    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            // 1、先展示缓存数据
            val cached = categoryRepository.getCached()
            if (cached.isNotEmpty()) {
                _uiState.update { it.copy(categories = cached) }
                loadBrands(0)
            }

            // 2、远程拉取最新数据（成功后自动替换 UI 数据）
            categoryRepository.refresh()
                .onSuccess { data ->
                    _uiState.update { it.copy(categories = data) }
                    loadBrands(0)
                }
                .onFailure { handleError(it) }
        }
    }

    /** key 已存在 → 跳过（Room 缓存命中，避免重复请求） */
    fun loadBrands(index: Int) {
        val cid = _uiState.value.categories.getOrNull(index)?.id ?: return
        if (index in _uiState.value.brandsMap) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val cached = brandRepository.getCached(cid)
            if (cached.isNotEmpty()) {
                _uiState.update { it.copy(brandsMap = it.brandsMap + (index to cached)) }
            }

            brandRepository.refresh(cid)
                .onSuccess { data -> _uiState.update { it.copy(brandsMap = it.brandsMap + (index to data)) } }
                .onFailure { handleError(it) }
        }
    }

    private fun handleError(e: Throwable) {
        when (e) {
            is ApiException -> {
                Log.w(TAG, e.toString(), e)
                _uiState.update { it.copy(errorMessage = ErrorMessage.Toast(e.message)) }
            }

            else -> {
                Log.e(TAG, e.toString(), e)
                _uiState.update { it.copy(errorMessage = ErrorMessage.Toast("${e.message}")) }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}