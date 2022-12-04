package com.example.capstone

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ServedOrderFragment(
    private var tableNum: TextView,
    private var tableType: TextView) : Fragment() {
    private lateinit var dbRef: FirebaseFirestore
    private lateinit var servedOrderArrayList: ArrayList<DataServedOrder>
    private lateinit var servedItemsList: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_served_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBackServedOrder)
        val txtTableTypeServedOrder = view.findViewById<TextView>(R.id.txtTableTypeServedOrder)
        val txtNumServedOrder = view.findViewById<TextView>(R.id.txtNumServedOrder)

        txtNumServedOrder.text = tableNum.text.toString()
        txtTableTypeServedOrder.text = tableType.text.toString()

        btnBack.setOnClickListener{
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SelectTableFragment())
                .commitNow()
        }
        val layoutManager = LinearLayoutManager(context)
        servedItemsList = view.findViewById(R.id.servedItemsList)
        servedItemsList.layoutManager =layoutManager
        servedOrderArrayList = arrayListOf()
        dbRef = FirebaseFirestore.getInstance()
        var queuePosition: Int = 0
        dbRef.collection("orderQueue").whereEqualTo("tableId", Integer.parseInt(tableNum.text.toString())).get().addOnSuccessListener {
            task ->
            for(document in task){
                queuePosition += 1
                if(document.exists()){
                    servedOrderArrayList.add(DataServedOrder(document.id.toString(),Integer.parseInt(tableNum.text.toString()), queuePosition))
                }
            }
            servedItemsList.adapter = ServedOrderAdapter(servedOrderArrayList, tableType, tableNum)
            Log.d("test", servedOrderArrayList.toString())
        }
    }
}