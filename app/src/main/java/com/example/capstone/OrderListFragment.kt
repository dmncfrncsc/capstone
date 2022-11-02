package com.example.capstone

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

class OrderListFragment(private var tableNums: TextView, private var tableTypes: TextView) : Fragment() {
    private lateinit var orderList: ArrayList<DataOrders>
    private lateinit var orderRecylcerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_orderlist,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val tableType: TextView = view.findViewById(R.id.txtTableTypeOrder)
        val tableNum:TextView = view.findViewById(R.id.txtTableNumOrder)
        val total: TextView = view.findViewById(R.id.txtTotalPrice)
        tableType.text = tableTypes.text.toString()
        tableNum.text = tableNums.text.toString()

        val btnCancel: Button = view.findViewById(R.id.btnBackCancel)
        val btnAddOrders: Button = view.findViewById(R.id.btnAddOrders)

        val layoutManger = LinearLayoutManager(context)
        orderRecylcerView = view.findViewById(R.id.orderListRecyclerview)
        orderRecylcerView.layoutManager = layoutManger
        orderList = arrayListOf()

        val arrayList = arrayListOf<DataCartList>()
        btnCancel.setOnClickListener{
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SelectTableFragment())
                .commitNow()
        }

        btnAddOrders.setOnClickListener{
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TakeOrderFragment(
                    tableNums,
                    tableTypes,
                    arrayList,
                    OrderListFragment(tableNums, tableTypes)
                ))
                .commitNow()
        }

        getOrderList(btnCancel,btnAddOrders,total )

    }

    private fun getOrderList(btnCancel: Button, btnAddOrders: Button, total: TextView) {
        val dbrefs = FirebaseFirestore.getInstance()


        dbrefs.collection("orders").addSnapshotListener(object: EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for(dc: DocumentChange in value?.documentChanges!!){
                    val data = dc.document.toObject(DataOrders::class.java)
                    var obj: Any? = null
                    var index: Int? = null
                    if(dc.type == DocumentChange.Type.ADDED){
                        orderList.add(data)
                        for (i in orderList) {
                            if(i.tableId != Integer.parseInt(tableNums.text.toString())){
                                orderList.remove(i)
                            }
                        }
                        orderRecylcerView.adapter = OrderListAdapter(orderList, btnAddOrders, total )

                    }
                    if(dc.type == DocumentChange.Type.MODIFIED){
                        for (i in orderList) {
                            if(i.tableId != Integer.parseInt(tableNums.text.toString())){
                                orderList.remove(i)
                                if (i.itemName == data.itemName) {
                                    obj = i
                                    index = orderList.indexOf(i)
                                }
                                if (obj != null) {

                                    orderList[index as Int] = data
                                }
                            }

                        }
                    }

                }

                orderRecylcerView.adapter = OrderListAdapter(orderList, btnAddOrders, total )
            }

        })

    }
}