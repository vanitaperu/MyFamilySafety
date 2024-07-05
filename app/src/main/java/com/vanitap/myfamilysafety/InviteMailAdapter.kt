package com.vanitap.myfamilysafety

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanitap.myfamilysafety.databinding.ItemInviteMailBinding

class InviteMailAdapter(
    private val listInvites: List<String>,
    private val onActionClick: OnActionClick
) : RecyclerView.Adapter<InviteMailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemInviteMailBinding.inflate(inflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listInvites[position]
        holder.bind(item, onActionClick)
    }

    override fun getItemCount(): Int {
        return listInvites.size
    }

    inner class ViewHolder(private val binding: ItemInviteMailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, onActionClick: OnActionClick) {
            binding.mail.text = item  // Bind the item to the mail TextView
            binding.accept.setOnClickListener {
                onActionClick.onAcceptClick(item)
            }
            binding.deny.setOnClickListener {
                onActionClick.onDenyClick(item)
            }
        }
    }

    interface OnActionClick {
        fun onAcceptClick(mail: String)
        fun onDenyClick(mail: String)
    }
}
