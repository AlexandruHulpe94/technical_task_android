package com.example.app.repository

import com.example.app.data.network.UsersApi
import com.example.app.data.network.model.User
import com.example.app.di.DaggerAppComponent
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class UserRepository {

    @Inject
    lateinit var usersApiService: UsersApi

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getLastPageUsers(): Single<List<User>> {
        return getPageCount().flatMap { page ->
            usersApiService.getUsers(page).map { response ->
                response.data.map { userResponse ->
                    User(
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
    //don't know why sometimes the api returns response 0
    fun createUser(user: User): Completable =
        usersApiService.addUser(user).flatMapCompletable { response ->
            if (response.code == 0 || response.code == 201) {
                Completable.complete()
            } else {
                Completable.error(Throwable("Some error on create" + response.code))
            }
        }

    //don't know why sometimes the api returns response 0
    fun delete(userId: Long) = usersApiService.deleteUser(userId).flatMapCompletable { response ->
        if (response.code == 0 || response.code == 204) {
            Completable.complete()
        } else {
            Completable.error(Throwable("Some error on delete" + response.code))
        }
    }


    private fun getPageCount(): Single<Int> {
        return usersApiService.getUsers().map {
            it.meta.pagination.pages
        }
    }
}