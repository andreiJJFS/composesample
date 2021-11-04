package com.jjfs.android.composetestapp.ui.screens.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.repository.AuthRepository
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _stateFlow = mutableStateOf(LoginScreenState())
    val stateFlow = _stateFlow

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    fun doLogin(pin: String) {
        authRepository.doLogin(pin)
            .catch { error ->
                _eventChannel.send(Event.ShowSnackBar(error.message ?: "Error"))
                _stateFlow.value = _stateFlow.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error"
                )
            }
            .onEach { result ->
                when (result) {
                    is OperationResult.Failure -> {
                        _eventChannel.send(Event.ShowSnackBar(result.reason.message ?: "Error"))
                        _stateFlow.value = LoginScreenState(
                            error = result.reason.message ?: "Error"
                        )
                    }
                    is OperationResult.InProgress ->
                        _stateFlow.value = LoginScreenState(isLoading = true)
                    is OperationResult.Success ->
                        _eventChannel.send(Event.DismissLogin)
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
