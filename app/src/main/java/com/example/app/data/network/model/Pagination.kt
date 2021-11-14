package com.example.app.data.network.model

data class Pagination(
    val total: Int,
    val pages: Int,
    val page: Int,
    val limit: Int
)