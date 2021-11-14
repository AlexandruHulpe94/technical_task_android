package com.example.app.data.network.model

data class UserResult(
    val code: Int,
    val meta: Meta,
    val data: List<User>
)