package com.example.app.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app.data.network.model.UserResponse
import com.example.app.databinding.ItemUserBinding

private val itemsDiffCallback = object : DiffUtil.ItemCallback<UserResponse>() {

    override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem == newItem
    }
}

class UserAdapter(private val longClickCallback: (UserResponse) -> Unit) :
    ListAdapter<UserResponse, UserAdapter.UserViewHolder>(itemsDiffCallback) {

    private val items = mutableListOf<UserResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(elements: List<UserResponse>) {
        items.clear()
        items.addAll(elements)
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserResponse) {
            binding.apply {
                userContainer.setOnLongClickListener {
                    longClickCallback.invoke(user)
                    return@setOnLongClickListener true
                }
                this.user = user
            }
        }
    }
}