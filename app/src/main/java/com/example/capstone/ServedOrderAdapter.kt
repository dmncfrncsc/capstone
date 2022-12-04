package com.example.capstone

import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ServedOrderAdapter(
    private val servedOrderArrayList: ArrayList<DataServedOrder>
) :
    RecyclerView.Adapter<ServedOrderAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.served_order_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = servedOrderArrayList[position]

        holder.orderNumber.text = currentItem.position.toString()
        holder.btnOrderQueue.setOnClickListener {
/*            val dbRef = FirebaseFirestore.getInstance()
            dbRef.collection("orders").whereEqualTo("queueID", currentItem.queueId).get()
                .addOnSuccessListener { task ->
                    for (document in task) {
                        val data = document.toObject(DataOrders::class.java)
                        holder.orderArrayList.add(data)

                    }
                }
            Log.d("TEST2", holder.orderArrayList.toString())*/

        }

    }


    override fun getItemCount(): Int {
        return servedOrderArrayList.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)/*,View.OnClickListener*/ {
        val orderArrayList: ArrayList<DataOrders> = arrayListOf()
        val orderNumber: TextView = itemView.findViewById(R.id.orderNumber)
        val btnOrderQueue: LinearLayout = itemView.findViewById(R.id.btnOrderQueue)
    }
}