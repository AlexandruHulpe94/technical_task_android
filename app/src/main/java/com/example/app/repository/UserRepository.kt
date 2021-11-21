package com.example.app.repository

import com.example.app.data.network.Callback
import com.example.app.data.network.UsersApi
import com.example.app.data.network.model.User
import com.example.app.data.network.model.UserResponse
import com.example.app.di.DaggerAppComponent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserRepository {

    @Inject
    lateinit var usersApiService: UsersApi

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getLastPageUsers(): Single<List<UserResponse>> {
        return getPageCount().flatMap { page ->
            usersApiService.getUsers(page).map { response ->
                response.data.map { userResponse ->
                    UserResponse(
                        userResponse.id,
                        userResponse.name,
                        userResponse.email,
                        userResponse.gender,
                        userResponse.status
                    )
                }
            }
        }
    }

    fun createUser(user: User, callback: Callback<UserResponse>) =
        usersApiService.addUser(user)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    callback.onSuccess(it)
                }, {
                    callback.onError(message = it.message.toString())
                })

    fun delete(userId: Long, callback: Callback<Unit?>) =
        usersApiService.deleteUser(userId).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    callback.onSuccess(null)
                }, {
                    callback.onError(message = it.message.toString())
                })


    private fun getPageCount(): Single<Int> {
        return usersApiService.getUsers().map {
            it.meta.pagination.pages
        }
    }
}