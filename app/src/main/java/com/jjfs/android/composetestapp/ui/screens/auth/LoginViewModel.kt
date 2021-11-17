package com.jjfs.android.composetestapp.ui.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjfs.android.composetestapp.business.domain.models.OperationResult.InProgress
import com.jjfs.android.composetestapp.business.domain.models.OperationResult.Success
import com.jjfs.android.composetestapp.business.domain.models.OperationResult.Failure
import com.jjfs.android.composetestapp.business.repository.AuthRepository
import com.jjfs.android.composetestapp.business.repository.SitesRepository
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _stateFlow = mutableStateOf(LoginScreenState())
    val stateFlow = _stateFlow

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    fun doLogin() {
        _stateFlow.value = LoginScreenState(isLoading = true)
        authRepository.doLogin()
            .onEach { result ->
                when(result) {
                    is Success -> {
                        _eventChannel.send(Event.DismissLogin)
                    }
                    is Failure -> {
                        _eventChannel.send(Event.ShowSnackBar(result.reason.message ?: "Error"))
                        _stateFlow.value = _stateFlow.value.copy(
                            isLoading = false,
                            error = result.reason.message ?: "Error"
                        )
                    }
                    is InProgress -> {
                        _stateFlow.value = _stateFlow.value.copy(isLoading = true)
                    }
                    else -> Unit
                }

            }
            .launchIn(viewModelScope)
    }
}

data class LoginScreenState(
    val isLoading: Boolean = false,
    val error: String = ""
)
