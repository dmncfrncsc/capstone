package com.example.capstone
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class CartFragment(
    private var orderList: ArrayList<DataCartList>,
    private var tableType: TextView,
    private var tableNum: TextView,
    private var currentActivity: Any?,
) : Fragment() {
    private lateinit var cartRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("TEST", orderList.toString())
        val btnCancel: Button = view.findViewById(R.id.btnBackCancel)
        val btnConfirmOrders: Button = view.findViewById(R.id.btnConfirmOrder)
        super.onViewCreated(view, savedInstanceState)
        val txtTotal: TextView = view.findViewById(R.id.txtTotalPrice)
        val txtTableType: TextView = view.findViewById(R.id.txtTableType)
        val txtTableNum: TextView = view.findViewById(R.id.txtTableNum)
        txtTableType.text = tableType.text.toString()
        txtTableNum.text = tableNum.text.toString()
        txtTotal.text = "TESTTT"
        val layoutManager = LinearLayoutManager(context)
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager = layoutManager
        cartRecyclerView.adapter = CartAdapter(orderList, txtTotal, btnCancel,tableType ,tableNum, btnConfirmOrders, currentActivity)
    }
}