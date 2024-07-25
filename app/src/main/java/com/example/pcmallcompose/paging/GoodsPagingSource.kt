package com.example.pcmallcompose.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.model.Goods
import com.example.pcmallcompose.service.GoodsService

class GoodsPagingSource(
    private val goodsService: GoodsService,
    private val query: String
) : PagingSource<Int, Goods>() {
    companion object {
        const val TAG = "GoodsPagingSource"
    }

    override fun getRefreshKey(state: PagingState<Int, Goods>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            val page = anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            Log.d(TAG, "重新加载page=$page")
            return page
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Goods> {
        try {
            val page = params.key ?: 1//从第一页开始
            val pageSize = params.loadSize
            val result = goodsService.searchGoodsList(query, page, pageSize)
            val data = result.data ?: HashMap()
            Log.d(TAG, "正在加载。。。")
            Log.d(TAG, "page=$page,pageSize=$pageSize,result=$result")
            val goodsList = JSON.parseArray(data["goodsList"], Goods::class.java)
            val totalCount = JSON.parseObject(data["totalCount"], Long::class.java)
            var totalPage = totalCount / pageSize
            if (totalCount % pageSize != 0L) {
                totalPage++
            }
            val prevPage = if (page > 1) page - 1 else null
            val nextPage = if (totalPage > page) page + 1 else null
            return LoadResult.Page(goodsList, prevPage, nextPage)
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }
}