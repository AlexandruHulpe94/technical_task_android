package com.example.app.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.data.network.model.User
import com.example.app.di.DaggerAppComponent
import com.example.app.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserViewModel : ViewModel() {

    @Inject
    lateinit var repository: UserRepository

    private val _users by lazy { MutableLiveData<List<User>>() }
    val users: LiveData<List<User>>
        get() = _users

    private val _loadUsersProgress by lazy { MutableLiveData<Boolean>() }
    val loadUsersProgress: LiveData<Boolean>
        get() = _loadUsersProgress

    private val _loadUsersError by lazy { MutableLiveData<Boolean>() }
    val loadUsersError: LiveData<Boolean>
        get() = _loadUsersError

    private val _addUserEvent by lazy { MutableLiveData<AddUserEvent>() }
    val addUserEvent: LiveData<AddUserEvent>
        get() = _addUserEvent

    private val _deleteUserEvent by lazy { MutableLiveData<DeleteUserEvent>() }
    val deleteUserEvent: LiveData<DeleteUserEvent>
        get() = _deleteUserEvent

    sealed class AddUserEvent {
        object Success : AddUserEvent()
        object Error : AddUserEvent()
        object EmptyEmail : AddUserEvent()
        object EmptyName : AddUserEvent()

    }

    sealed class DeleteUserEvent {
        object Success : DeleteUserEvent()
        object Error : DeleteUserEvent()
    }

    private val compositeDisposable = CompositeDisposable()

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun getUsers() {
        compositeDisposable.add(
            repository.getLastPageUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ users ->
                    _loadUsersProgress.postValue(true)
                    _users.postValue(users)
                    _loadUsersProgress.postValue(false)
                    _loadUsersError.postValue(false)
                }) {
                    _loadUsersProgress.postValue(true)
                    Log.e(TAG, it.message ?: "Error message")
                    _loadUsersError.postValue(true)
                    _loadUsersProgress.postValue(false)
                })
    }

    fun deleteUser(userId: Long): Completable {
        compositeDisposable.add(
            repository.delete(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        _deleteUserEvent.value = DeleteUserEvent.Success
                    },
                    {
                        Log.d(TAG, it.message ?: "Error message")
                        _deleteUserEvent.value = DeleteUserEvent.Error
                    })
        )
        if (_deleteUserEvent.value == DeleteUserEvent.Success) {
            return Completable.complete()
        }
        return Completable.error(Throwable("Something went wrong"))
    }

    fun createUser(name: String?, email: String?): Completable {
        if (name.isNullOrBlank()) {
            _addUserEvent.value = AddUserEvent.EmptyName
            return Completable.error(Throwable("Empty name"))
        }
        if (email.isNullOrBlank()) {
            _addUserEvent.value = AddUserEvent.EmptyEmail
            return Completable.error(Throwable("Empty email"))
        }
        compositeDisposable.add(
            repository.createUser(User(System.currentTimeMillis(), name, email, "male", "active"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        _addUserEvent.value = AddUserEvent.Success
                    }, {
                        _addUserEvent.value = AddUserEvent.Error
                    })
        )
        if (_addUserEvent.value == AddUserEvent.Success) {
            return Completable.complete()
        }
        return Completable.error(Throwable("Something went wrong"))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}