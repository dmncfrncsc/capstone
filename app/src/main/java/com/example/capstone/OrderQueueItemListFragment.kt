package com.example.capstone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class OrderQueueItemListFragment(
    private val orderNumber: String,
    private val queueId: String?,
    private val tableType: TextView,
    private val tableNumber: TextView
) : Fragment() {
    private lateinit var itemArrayList: ArrayList<DataOrders>
    private lateinit var itemRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_queue_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val txtOrderNumber: TextView = view.findViewById(R.id.txtOrderNumber)
        txtOrderNumber.text = orderNumber.toString()
        val btnBackServe: ImageButton = view.findViewById(R.id.btnBackServe)
        val btnServe: Button = view.findViewById(R.id.btnServe)
        itemArrayList = arrayListOf()
        itemRecyclerView = view.findViewById(R.id.ordersByQueueList)
        itemRecyclerView.layoutManager = LinearLayoutManager(context)

        val dbRef = FirebaseFirestore.getInstance()
        dbRef.collection("orders").whereEqualTo("queueID", queueId).get()
            .addOnSuccessListener { task ->
                for (document in task) {
                    val data = document.toObject(DataOrders::class.java)
                    itemArrayList.add(data)
                }
                itemRecyclerView.adapter = OrderQueueItemListAdapter(itemArrayList)
            }

        btnServe.setOnClickListener {
            val DataRef = FirebaseFirestore.getInstance()
            DataRef.collection("orderQueue").document(queueId!!).delete()
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersServedFragment()).commitNow()
        }
        btnBackServe.setOnClickListener {
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ServedOrderFragment(tableNumber,tableType)).commitNow()
        }
    }
}