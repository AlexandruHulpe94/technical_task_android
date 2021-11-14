package com.example.app.data.network

import com.example.app.data.network.model.UserResponse
import com.example.app.data.network.model.UserResult
import com.example.app.data.network.model.User
import com.example.app.internal.API_KEY
import io.reactivex.Single
import retrofit2.http.*

interface UsersApi {
    //API DOESN'T WORK PROPERLY AND DOESN'T RETURN USERS WITH CREATION TIME
    @GET("users")
    fun getUsers(@Query(value = "page") pageNumber: Int? = null): Single<UserResult>

    @Headers("Authorization: $API_KEY")
    @DELETE("users/{id}")
    fun deleteUser(@Path("id") userId: Long): Single<UserResponse>

    @Headers("Authorization: $API_KEY")
    @POST("users")
    fun addUser(@Body user: User): Single<UserResponse>

}