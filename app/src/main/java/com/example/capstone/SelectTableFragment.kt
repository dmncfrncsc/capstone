package com.example.capstone

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class SelectTableFragment: Fragment() {
    private lateinit var dbref: DatabaseReference
    private lateinit var tableRecyclerView: RecyclerView
    private lateinit var tableArrayList: ArrayList<DataTable>
    lateinit var table: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_table,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        tableRecyclerView = view.findViewById(R.id.tableList)
        tableRecyclerView.layoutManager = layoutManager
        tableRecyclerView.setHasFixedSize(true)
        tableArrayList = arrayListOf<DataTable>()

        getTableList()


    }


    private fun getTableList() {

        dbref = FirebaseDatabase.getInstance().getReference("Dine").child("Tables")
        dbref.addValueEventListener(object: ValueEventListener,
            tableAdapter.OnItemClickListener {

            override fun onDataChange(p0: DataSnapshot) {
                tableArrayList.clear();
                if(p0.exists()){
                    for (tableSnapshot in p0.children){
                        val table = tableSnapshot.getValue(DataTable::class.java)
                        tableArrayList.add(table!!)
                    }
                }


                tableRecyclerView.adapter  = tableAdapter(tableArrayList,this)

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(position: Int) {
                var currentITem = tableArrayList[position]

                var tableId = currentITem.id
                var color = currentITem.color
                showDialog(position, tableId, color)

            }
        })

    }
    private fun showDialog(tableNum: Int, tableId: Int?, color: String?){

        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.popup_table_options)
        table = dialog.findViewById(R.id.tableNum)

        table.text = (tableNum+1).toString()

        tableArrayList.clear()
        tableArrayList.add(DataTable(tableId,color))

        var takeOrder: Button = dialog.findViewById(R.id.btnTakeOrder)

        takeOrder.setOnClickListener{

           var showTakeOrderFragment: FragmentTransaction = parentFragmentManager.beginTransaction()
            showTakeOrderFragment.replace(R.id.fragment_container, TakeOrderFragment(table))
            showTakeOrderFragment.commit()
            var tool: Toolbar = requireActivity().findViewById(R.id.toolbar)
            tool.title = "TRANOS"
            dialog.dismiss()
        }
        dialog.show()
    }



}