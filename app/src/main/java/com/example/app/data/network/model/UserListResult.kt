package com.example.app.data.network.model

data class UserListResult(
    val code: Int,
    val meta: Meta,
    val data: List<UserResponse>
)