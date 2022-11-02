package com.example.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class OrderListAdapter(
    private val orderList: ArrayList<DataOrders>,
    private val btnAddOrders: Button,
    private val total: TextView,

) :
    RecyclerView.Adapter<OrderListAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.orders_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dec = DecimalFormat("0.00")
        val currentItem = orderList[position]
        var price:Long = 0;

        holder.txtItemName.text =currentItem.itemName.toString()
        holder.txtQty.text = currentItem.quantity.toString()
        holder.txtPrice.text = dec.format(currentItem.price.toString().toDouble())
        holder.subtotal.text = dec.format(currentItem.subTotal.toString().toDouble())
        for(o in orderList){
            price += o.subTotal!!;
        }
        total.text =dec.format(price)

        if (currentItem.imageUrl!!.isNotEmpty()) {
            Picasso.with(holder.itemView.context).load(currentItem.imageUrl).fit().into(holder.orderImage)

        }else{

        }

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtItemName:TextView = itemView.findViewById(R.id.txtItemName)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val txtQty: TextView = itemView.findViewById(R.id.txtQty)
        val subtotal: TextView = itemView.findViewById(R.id.txtSubtotal)
        val orderImage: ImageView = itemView.findViewById(R.id.ordersImage)
    }
}