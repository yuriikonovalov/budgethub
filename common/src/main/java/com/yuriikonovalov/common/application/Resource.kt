package com.yuriikonovalov.common.application

import java.io.IOException


sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure<T>(val error: Exception? = null) : Resource<T>()

    inline fun onSuccess(body: (data: T) -> Unit): Resource<T> {
        if (this is Success) {
            body(this.data)
        }
        return this
    }

    inline fun onFailure(body: (error: Exception?) -> Unit): Resource<T> {
        if (this is Failure) {
            body(error)
        }
        return this
    }

    companion object {
        fun unit() = Success(Unit)
        fun <T> successIfNotNull(
            data: T?, exception: Exception = IOException()
        ): Resource<T> {
            return data?.let { Success(it) } ?: Failure(exception)
        }

        fun <T> successIfNotEmpty(
            list: List<T>, exception: Exception = IOException()
        ): Resource<List<T>> {
            return if (list.isEmpty()) {
                Failure(exception)
            } else {
                Success(list)
            }
        }
    }
}