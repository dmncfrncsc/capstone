package com.example.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter (private val cartList: ArrayList<DataCartList>): RecyclerView.Adapter<CartAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = cartList[position]
        holder.itemName.text = currentItem.MealName
        holder.qty.text = currentItem.qty.toString()
        holder.itemView.setBackgroundResource(R.drawable.shape_button_rounded)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    inner class MyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val itemName: TextView = itemView.findViewById(R.id.cartItemName)
        val qty: TextView = itemView.findViewById(R.id.cartItemQty)
    }

}