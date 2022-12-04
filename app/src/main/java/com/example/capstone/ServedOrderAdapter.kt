package com.example.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ServedOrderAdapter(
    private val servedOrderArrayList: ArrayList<DataServedOrder>,
    private val tableType: TextView,
    private val tableNum: TextView
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
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container,
                    OrderQueueItemListFragment(holder.orderNumber.text.toString(), currentItem.queueId, tableType, tableNum)
                ).commitNow()
/*
           val dbRef = FirebaseFirestore.getInstance()
            dbRef.collection("orders").whereEqualTo("queueID", currentItem.queueId).get()
                .addOnSuccessListener { task ->
                    for (document in task) {
                        val data = document.toObject(DataOrders::class.java)
                        holder.orderArrayList.add(data)

                    }
                }
*/


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