package com.jjfs.android.composetestapp.business.domain.models

sealed class OperationResult<out T, out E> {
    object Empty: OperationResult<Nothing, Nothing>()
    data class Success<out T>(val value: T) : OperationResult<T, Nothing>()
    data class Failure<out E>(val reason: E) : OperationResult<Nothing, E>()
    data class InProgress(val value: Boolean = false): OperationResult<Nothing, Nothing>()
}
/**
 * Map a function over the _value_ of a successful Result.
 */
inline fun <T, Tʹ, E> OperationResult<T, E>.map(f: (T) -> Tʹ): OperationResult<Tʹ, E> =
    flatMap { value -> OperationResult.Success(f(value)) }

/**
 * Flat-map a function over the _value_ of a successful Result.
 */
inline fun <T, Tʹ, E> OperationResult<T, E>.flatMap(f: (T) -> OperationResult<Tʹ, E>): OperationResult<Tʹ, E> =
    when (this) {
        is OperationResult.Success<T> -> f(value)
        is OperationResult.Failure -> this
        is OperationResult.InProgress -> this
        else -> OperationResult.Empty
    }

/**
 * Unwrap a Result in which both the success and failure values have the same type, returning a plain value.
 */
inline fun <T> OperationResult<T, T>.get() = when (this) {
    is OperationResult.Success<T> -> value
    is OperationResult.InProgress -> value
    is OperationResult.Failure<T> -> reason
    else -> Unit
}

/**
 * Call a function and wrap the result in a Result, catching any Exception and returning it as Err value.
 */
inline fun <T> operationResultFrom(block: () -> T): OperationResult<T, Exception> =
    try {
        OperationResult.Success(block())
    }
    catch (ex: Exception) {
        OperationResult.Failure(ex)
    }

/**
 * Perform a side effect with the success value.
 */
inline fun <T, E> OperationResult<T, E>.onSuccess(f: (T) -> Unit) =
    apply { if (this is OperationResult.Success<T>) f(value) }

/**
 * Perform a side effect with the inProgress value.
 */
inline fun <T, E> OperationResult<T, E>.onInProgress(f: () -> Unit) =
    apply { if (this is OperationResult.InProgress) f() }

/**
 * Perform a side effect with the failure reason.
 */
inline fun <T, E> OperationResult<T, E>.onFailure(f: (E) -> Unit) =
    apply { if (this is OperationResult.Failure<E>) f(reason) }

