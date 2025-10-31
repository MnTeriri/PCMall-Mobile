//package com.example.pcmallcompose.paging
//
//import android.util.Log
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import kotlinx.coroutines.async
//import kotlinx.coroutines.awaitAll
//import kotlinx.coroutines.coroutineScope
//
//class BasePagingSource<T : Any>(
//    private val getPagingData: (page: Int, pageSize: Int) -> List<T>,
//    private val getTotalCount: () -> Long
//) : PagingSource<Int, T>() {
//    companion object {
//        const val TAG = "BasePagingSource"
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            val page = anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//            Log.d(TAG, "重新加载page=$page")
//            return page
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
//        try {
//            val page = params.key ?: 1//从第一页开始
//            val pageSize = params.loadSize
//            val result: List<Any> = coroutineScope {
//                val asyncPagingData = async { getPagingData(page, pageSize) }
//                val asyncTotalCount = async { getTotalCount() }
//                return@coroutineScope awaitAll(asyncPagingData, asyncTotalCount)
//            }
//            val pagingData = result[0] as List<T>
//            val totalCount = result[1] as Long
//            var totalPage = totalCount / pageSize
//            if (totalCount % pageSize != 0L) {
//                totalPage++
//            }
//            Log.d(TAG, "page=$page totalPage=$totalPage totalCount=$pageSize")
//            val prevPage = if (page > 1) page - 1 else null
//            val nextPage = if (totalPage > page) page + 1 else null
//            return LoadResult.Page(pagingData, prevPage, nextPage)
//        } catch (e: Exception) {
//            Log.e(TAG, "出现错误：$e")
//            return LoadResult.Error(e)
//        }
//    }
//}