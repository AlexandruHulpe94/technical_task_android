package com.example.app

import com.example.app.data.network.Callback
import com.example.app.data.network.UsersApi
import com.example.app.data.network.model.*
import com.example.app.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class UserViewModelTest {

    @InjectMocks
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var userService: UsersApi

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testPagination() {
        `when`(userService.getUsers(0)).thenReturn(
            Single.just(
                UserListResult(
                    200, Meta(Pagination(1, 2, 3, 4)),
                    listOf(
                        UserResponse(
                            213L, "some name",
                            "some email",
                            "male",
                            "active"
                        )
                    )
                )
            )
        )
        `when`(userService.getUsers()).thenReturn(
            Single.just(
                UserListResult(
                    200, Meta(Pagination(1, 2, 3, 4)),
                    listOf(
                        UserResponse(
                            213L, "some name",
                            "some email",
                            "male",
                            "active"
                        )
                    )
                )
            )
        )
        userRepository.getLastPageUsers()
        verify(userService, times(1)).getUsers()
    }

    @Test
    fun testCreateUser() {
        `when`(
            userService.addUser(
                User(
                    "some name",
                    "some email",
                    "male",
                    "active"
                )
            )
        ).thenReturn(
            Single.just(
                UserResponse(
                    213L, "some name",
                    "some email",
                    "male",
                    "active"
                )
            )
        )
        userRepository.createUser(User(
            "some name",
            "some email",
            "male",
            "active"
        ), object : Callback<UserResponse> {
            override fun onSuccess(response: UserResponse) {

            }

            override fun onError(message: String) {
            }
        })
        verify(userService, times(1)).addUser(
            User(
                "some name",
                "some email",
                "male",
                "active"
            )
        )
    }

    @Test
    fun testDeleteUser() {
        `when`(
            userService.deleteUser(
                123L
            )
        ).thenReturn(
            Completable.complete()
        )
        userRepository.delete(123L, object : Callback<Unit?> {
            override fun onSuccess(response: Unit?) {

            }

            override fun onError(message: String) {
            }
        })
        verify(userService, times(1)).deleteUser(
            123L
        )
    }
}