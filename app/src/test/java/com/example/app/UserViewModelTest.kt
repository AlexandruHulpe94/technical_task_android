package com.example.app

import com.example.app.data.network.model.User
import com.example.app.repository.UserRepository
import com.example.app.viewmodel.UserViewModel
import io.reactivex.Completable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class UserViewModelTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testCreateUserRepoGetCalled() {
        val userViewModel: UserViewModel = spy(UserViewModel())

        userViewModel.createUser("some name", "some email")
        verify(userRepository, times(1)).createUser(
            User(
                23241L,
                "some name",
                "some email",
                "male",
                "active"
            )
        )
    }

    @Test
    fun testDeleteUserRepoGetCalled() {
        val userViewModel: UserViewModel = spy(UserViewModel())

        userViewModel.deleteUser(123L)
        verify(userRepository, times(1)).delete(
            123L
        )
    }

    @Test
    fun testGetUserSRepoGetCalled() {
        val userViewModel: UserViewModel = spy(UserViewModel())

        userViewModel.getUsers()
        verify(userRepository, times(1)).getLastPageUsers()
    }

    @Test
    fun successCreateUser() {
        val userViewModel: UserViewModel = spy(UserViewModel())
        `when`(
            userRepository.createUser(
                User(
                    23241L,
                    "some name",
                    "some email",
                    "male",
                    "active"
                )
            )
        )
            .thenReturn(Completable.complete())

        userViewModel.createUser("testName", "test@email.com")
            .test()
            .assertComplete()
    }

    @Test
    fun errorCreateUser() {
        val userViewModel: UserViewModel = spy(UserViewModel())
        `when`(
            userRepository.createUser(
                User(
                    23241L,
                    "some name",
                    "some email",
                    "male",
                    "active"
                )
            )
        )
            .thenReturn(Completable.error(Throwable("Some error")))

        userViewModel.createUser("testName", "test@email.com")
            .test()
            .assertError(Throwable("Some error"))
    }

    @Test
    fun successDeleteUser() {
        val userViewModel: UserViewModel = spy(UserViewModel())
        `when`(userRepository.delete(23241L))
            .thenReturn(Completable.complete())

        userViewModel.deleteUser(23241L)
            .test()
            .assertComplete()
    }

    @Test
    fun errorDeleteUser() {
        val userViewModel: UserViewModel = spy(UserViewModel())
        `when`(userRepository.delete(23241L))
            .thenReturn((Completable.error(Throwable("Some error"))))

        userViewModel.deleteUser(23241L)
            .test()
            .assertError(Throwable("Some error"))
    }
}