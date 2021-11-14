package com.example.app.view

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.app.R
import com.example.app.data.network.model.User
import com.example.app.di.DaggerAppComponent
import com.example.app.view.adapter.UserAdapter
import com.example.app.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var userAdapter: UserAdapter

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerAppComponent.create().inject(this)

        setUpListeners()

        observeLiveData()

        viewModel.getUsers()
    }

    private fun setUpListeners() {
        userAdapter = UserAdapter(longClickCallback = { user -> onLongClickPressed(user) })
        recycler_view.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = userAdapter
        }

        add_user_button.setOnClickListener { showCreateUserDialog() }
    }

    private fun observeLiveData() {
        observeInProgress()
        observeIsError()
        observeUsersList()
        observeCreateUser()
        observeDeleteUser()
    }

    private fun observeDeleteUser() {
        viewModel.deleteUserEvent.observe(this, Observer { deleteUserEvent ->
            when (deleteUserEvent) {
                UserViewModel.DeleteUserEvent.Error -> Toast.makeText(
                    this,
                    getString(R.string.delete_user_error_text),
                    Toast.LENGTH_LONG
                ).show()
                UserViewModel.DeleteUserEvent.Success -> {
                    Toast.makeText(
                        this,
                        getString(R.string.delete_user_success_text),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.getUsers()
                }
            }
        })
    }

    private fun observeCreateUser() {
        viewModel.addUserEvent.observe(this, Observer { addUserEvent ->
            when (addUserEvent) {
                UserViewModel.AddUserEvent.Error -> Toast.makeText(
                    this,
                    getString(R.string.add_user_error_text),
                    Toast.LENGTH_LONG
                ).show()
                UserViewModel.AddUserEvent.Success -> {
                    Toast.makeText(
                        this,
                        getString(R.string.add_user_success_text),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.getUsers()
                }
                UserViewModel.AddUserEvent.EmptyEmail -> Toast.makeText(
                    this,
                    getString(R.string.add_user_empty_email_text),
                    Toast.LENGTH_LONG
                ).show()
                UserViewModel.AddUserEvent.EmptyName -> Toast.makeText(
                    this,
                    getString(R.string.add_user_empty_name_text),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun observeInProgress() {
        viewModel.loadUsersProgress.observe(this, Observer { isLoading ->
            isLoading.let {
                if (it) {
                    empty_text.visibility = View.GONE
                    recycler_view.visibility = View.GONE
                    fetch_progress.visibility = View.VISIBLE
                } else {
                    fetch_progress.visibility = View.GONE
                }
            }
        })
    }

    private fun observeIsError() {
        viewModel.loadUsersError.observe(this, Observer { isError ->
            isError.let {
                if (it) {
                    disableViewsOnError()
                } else {
                    empty_text.visibility = View.GONE
                    fetch_progress.visibility = View.GONE
                }
            }
        })
    }

    private fun disableViewsOnError() {
        fetch_progress.visibility = View.VISIBLE
        empty_text.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE
        userAdapter.setItems(emptyList())
        fetch_progress.visibility = View.GONE
    }

    private fun observeUsersList() {
        viewModel.users.observe(this, Observer { users ->
            users.let {
                if (it != null && it.isNotEmpty()) {
                    fetch_progress.visibility = View.VISIBLE
                    recycler_view.visibility = View.VISIBLE
                    userAdapter.setItems(it)
                    empty_text.visibility = View.GONE
                    fetch_progress.visibility = View.GONE
                } else {
                    disableViewsOnError()
                }
            }
        })
    }

    private fun onLongClickPressed(user: User) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.title_alert_dialog)
        alertDialogBuilder.setMessage(R.string.message_alert_dialog)
        alertDialogBuilder.setPositiveButton(
            R.string.positive_button_title
        ) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            viewModel.deleteUser(userId = user.id)
        }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showCreateUserDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Title")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val name = EditText(this)
        name.hint = getString(R.string.enter_name)
        name.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(name)

        val email = EditText(this)
        email.hint = getString(R.string.enter_email)
        email.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(email)

        builder.setView(layout)

        builder.setPositiveButton("OK") { _, _ ->
            viewModel.createUser(name.text.toString(), email.text.toString())
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}