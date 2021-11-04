package com.jjfs.android.composetestapp.business.repository

import android.util.Log
import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.domain.models.User
import com.jjfs.android.composetestapp.business.network.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AuthRepositoryInterface {
    fun doLogin(pin: String): Flow<OperationResult<User, Exception>>
    fun doLogout()
    fun setUser(user: User)
    fun getUser(): User
    fun getAccessToken(): String
}

class AuthRepository(private val authService: AuthService): AuthRepositoryInterface {
    private var currentUser: User? = null

    override fun doLogin(pin: String): Flow<OperationResult<User, Exception>> = flow {
        emit(OperationResult.InProgress())
        val result = authService.login(pin)
        if(result.employeeId.isEmpty()) emit(OperationResult.Failure(Exception("Error")))
        else {
            setUser(result)
            emit(OperationResult.Success(result))
        }
        Log.i("AuthRepository", "login completed")
    }

    override fun doLogout() {
        this.currentUser = null
    }

    override fun setUser(user: User) {
        this.currentUser = user.copy()
    }

    override fun getUser(): User = this.currentUser ?: throw UserNotAuthenticatedException("No user logged in")

    override fun getAccessToken() : String =
        this.currentUser?.accessToken ?: throw UserNotAuthenticatedException("Login expired")
}

class UserNotAuthenticatedException(message: String = ""): java.lang.Exception(message)
