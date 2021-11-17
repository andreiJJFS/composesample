package com.jjfs.android.composetestapp.business.repository

import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.domain.models.OperationResult.Failure
import com.jjfs.android.composetestapp.business.domain.models.OperationResult.Success
import com.jjfs.android.composetestapp.business.domain.models.OperationResult.InProgress
import com.jjfs.android.composetestapp.business.domain.models.onSuccess
import com.jjfs.android.composetestapp.business.domain.models.operationResultFrom
import com.jjfs.android.composetestapp.business.network.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AuthRepositoryInterface {
    fun getAccessToken(): String
    fun saveAccessToken(token: String)
}

class AuthRepository(private val authService: AuthService): AuthRepositoryInterface {
    private var graphqlToken = ""

    fun doLogin(): Flow<OperationResult<String, Exception>> = flow {
        emit(InProgress())
        val result = operationResultFrom { authService.login() }
        when(result) {
            is Success -> {
                saveAccessToken(result.value)
                emit(result)
            }
            is Failure -> emit(Failure(result.reason))
            else -> Unit
        }
    }

    override fun getAccessToken() : String =
        if(graphqlToken.isEmpty()) throw UserNotAuthenticatedException("Login expired")
        else graphqlToken

    override fun saveAccessToken(token: String) {
        graphqlToken = token
    }
}

class UserNotAuthenticatedException(message: String = ""): java.lang.Exception(message)
