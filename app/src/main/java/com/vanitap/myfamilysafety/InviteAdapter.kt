package com.d4d5.myfamilySfety

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanitap.myfamilysafety.ContactModel
import com.vanitap.myfamilysafety.R
import com.vanitap.myfamilysafety.databinding.ItemInviteBinding


class InviteAdapter(private val listContacts: List<ContactModel>) :
    RecyclerView.Adapter<InviteAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = ItemInviteBinding.inflate(inflater,parent,false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listContacts[position]
        holder.name.text= item.name

    }

    override fun getItemCount(): Int {
        return listContacts.size
    }

    class ViewHolder(private val item: ItemInviteBinding) : RecyclerView.ViewHolder(item.root) {
        val name = item.name

    }
}