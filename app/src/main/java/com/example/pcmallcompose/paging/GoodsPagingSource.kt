package com.example.pcmallcompose.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alibaba.fastjson2.JSON
import com.example.pcmallcompose.model.Goods
import com.example.pcmallcompose.service.GoodsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

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
            //runBlocking所在协程被挂起后会阻塞所在线程，线程不能处理协程之外的事情；
            //coroutineScope所在的协程被挂起后，则会立即交出控制权给所在的线程，不会阻塞线程，线程可以处理协程之外的事情。
            //coroutineScope是一个挂起函数，它创建一个新的协程作用域并在该作用域内启动协程。
            //它会等待所有子协程完成后才会继续执行后续代码。 coroutineScope主要用于限制子协程的生命周期与父协程相同。
            val result = coroutineScope {
                val asyncGoodsList = async {
                    return@async goodsService.searchGoodsList(query, page, pageSize).data!!
                }
                val asyncTotalCount = async {
                    return@async goodsService.getTotalCount(query).data!!
                }
                return@coroutineScope awaitAll(asyncGoodsList, asyncTotalCount)
            }
            Log.d(TAG, "正在加载。。。")
            Log.d(TAG, "page=$page,pageSize=$pageSize,result=$result")
            val goodsList = result[0] as List<Goods>
            val totalCount = result[1] as Long
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