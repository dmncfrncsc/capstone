package com.example.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class OrderQueueItemListAdapter(
    private val itemArrayList: ArrayList<DataOrders>) :
    RecyclerView.Adapter<OrderQueueItemListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.orders_queue_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemArrayList[position]

        holder.itemName.text = currentItem.itemName

        if (currentItem.imageUrl.toString() != "null") {
            Picasso.with(holder.itemView.context).load(currentItem.imageUrl).fit().into(holder.ivIcon)
        }
    }


    override fun getItemCount(): Int {
        return itemArrayList.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val itemName:TextView = itemView.findViewById(R.id.itemName)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
    }
}