package com.example.app.data.network.model

//API DOESN'T WORK PROPERLY AND DOESN'T RETURN USERS WITH CREATION TIME
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val gender: String,
    val status: String
)