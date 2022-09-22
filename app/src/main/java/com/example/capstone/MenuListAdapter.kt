package com.example.capstone

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class MenuListAdapter(private val menuList:ArrayList<menu_meal>,
                      private val listener: OnItemClickListner):
    RecyclerView.Adapter<MenuListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_menu_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = menuList[position]
        val dec = DecimalFormat("₱#,###.00")
        holder.itemName.text = currentItem.MealName
        holder.price.text = dec.format(currentItem.Price).toString()
        holder.stock.text = currentItem.Status.toString()


        if(holder.stock.text.toString() == "true"){
            holder.stock.text="Available"
            holder.itemView.setBackgroundColor(Color.parseColor("#3f9c02"))
        }else{
            holder.stock.text="Sold out"
            holder.itemView.setBackgroundColor(Color.parseColor("#9c0202"))
        }


    }

    override fun getItemCount(): Int {
       return menuList.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val stock: TextView = itemView.findViewById(R.id.tvStatus)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if(position!=RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }

        }
    }
    interface OnItemClickListner{
        fun onItemClick(position: Int)
    }
}