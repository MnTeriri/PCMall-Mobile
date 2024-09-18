package com.example.pcmallcompose.paging;

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pcmallcompose.model.Cart

class CartPagingSource(

): PagingSource<Int, Cart>() {
    override fun getRefreshKey(state: PagingState<Int, Cart>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cart> {
        TODO("Not yet implemented")
    }
}
