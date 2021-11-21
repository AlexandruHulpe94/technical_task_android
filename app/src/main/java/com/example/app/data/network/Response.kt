package com.example.app.data.network


interface Callback<T> {
    fun onSuccess(response: T)

    fun onError(message: String)
}