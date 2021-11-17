package com.jjfs.android.composetestapp.ui.screens.graphql

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.repository.LaunchDetails
import com.jjfs.android.composetestapp.business.repository.Site
import com.jjfs.android.composetestapp.business.repository.SitesRepository
import com.jjfs.android.composetestapp.business.repository.UserNotAuthenticatedException
import com.jjfs.android.composetestapp.ui.components.CommonViewModel
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class GraphqlViewModel(private val sitesRepository: SitesRepository): CommonViewModel() {

    private val _stateFlow = MutableStateFlow(GraphqlViewModelState())
    val stateFlow = _stateFlow.asStateFlow()

    val sites: Flow<PagingData<Site>> =
        Pager(PagingConfig(pageSize = 20)) {
            SitesDataSource(
                repository = sitesRepository,
                onSuccess = { sites ->
                    _stateFlow.value =
                        _stateFlow.value.copy(
                            isLoading = false,
                            sites = sites
                        )
                },
                onInProgress = { setLoadingState() },
                onFailure = { ex: Exception ->
                    viewModelScope.launch {
                        handleOperationFailure(ex)
                    }
                }
            )
        }.flow
            .catch { error -> handleOperationFailure(error) }
            .cachedIn(viewModelScope)

    init {
        sitesRepository.bookingsFlow.onEach {
            val text = when (val trips = it.data?.tripsBooked) {
                    null -> "Subscription error"
                    -1 -> "Trip cancelled"
                    else -> "Trip booked -  trips: $trips"
                }
                _eventChannel.emit(Event.ShowSnackBar(text))
        }
        .launchIn(viewModelScope)
    }

    fun getSiteDetails(id: String) {
        sitesRepository.fetchSiteDetail(id)
            .onEach { result ->
                when (result) {
                    is OperationResult.Failure -> handleOperationFailure(result.reason)
                    is OperationResult.Success -> handleOperationSuccess(result.value)
                    else -> setLoadingState()
                }
            }
            .launchIn(viewModelScope)
    }

    fun book() {
        val id = _stateFlow.value.siteDetails?.id ?: ""
        sitesRepository.bookTrip(id)
            .catch { e -> Log.d("graphql viewmodel", e.message ?: "Error") }
            .onEach { result ->
                when(result) {
                    is OperationResult.Failure -> handleOperationFailure(result.reason)
                    is OperationResult.Success -> getSiteDetails(id)
                    else -> setLoadingState()
                }
            }
            .launchIn(viewModelScope)
    }

    fun cancel() {
        val id = _stateFlow.value.siteDetails?.id ?: ""
        sitesRepository.cancelTrip(id)
            .catch { e -> Log.d("graphql viewmodel", e.message ?: "Error") }
            .onEach { result ->
                when(result) {
                    is OperationResult.Failure -> handleOperationFailure(result.reason)
                    is OperationResult.Success -> getSiteDetails(id)
                    else -> setLoadingState()
                }
            }
            .launchIn(viewModelScope)
    }

    fun clearSite() {
        _stateFlow.value = _stateFlow.value.copy(siteDetails = null)
    }

    private fun handleOperationSuccess(value: LaunchDetails) {
        _stateFlow.value =
            _stateFlow.value.copy(
                isLoading = false,
                siteDetails = value
            )
    }

    private suspend fun handleOperationFailure(reason: Throwable) {
        val error = reason.message ?: "Error"
        _eventChannel.emit(Event.ShowSnackBar(error))

        if(reason is UserNotAuthenticatedException) _eventChannel.emit(Event.OpenLogin())
        else _stateFlow.value = GraphqlViewModelState(error = error)
    }

    private fun setLoadingState() {
        _stateFlow.value = _stateFlow.value.copy(isLoading = true)
    }
}

data class GraphqlViewModelState(
    val isLoading: Boolean = false,
    val sites: List<Site> = emptyList(),
    val siteDetails: LaunchDetails? = null,
    val error: String = ""
)
