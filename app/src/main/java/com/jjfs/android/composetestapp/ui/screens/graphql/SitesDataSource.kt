package com.jjfs.android.composetestapp.ui.screens.graphql

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.repository.Site
import com.jjfs.android.composetestapp.business.repository.SitesRepository
import kotlinx.coroutines.flow.collect

class SitesDataSource(
    private val repository: SitesRepository,
    private val onSuccess: (data: List<Site>) -> Unit = {},
    private val onFailure: (ex: Exception) -> Unit = {},
    private val onInProgress: () -> Unit,
): PagingSource<Int, Site>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Site> {
        onInProgress()
        return when(val sitesResponse = repository.fetchSites()) {
            is OperationResult.Success -> {
                val pageNumber = params.key ?: 0
                val prevKey = if (pageNumber > 0) pageNumber - 1 else null
                val nextKey = prevKey?.plus(1)
                LoadResult.Page(
                    data = sitesResponse.value.also { onSuccess(it) },
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            is OperationResult.Failure -> {
                onFailure(sitesResponse.reason)
                LoadResult.Error(sitesResponse.reason)
            }
            else -> LoadResult.Error(throwable = Throwable("Unexpected error"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Site>): Int? {
        return null
    }
}
