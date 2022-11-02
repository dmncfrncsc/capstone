package com.example.capstone


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class TransferTableFragment(private val tableNums: TextView, private val tableTypes: TextView) : Fragment() {
    private lateinit var  dbRef: DatabaseReference
    private lateinit var  tableRecyclerView: RecyclerView
    private  lateinit var  tableArrayList: ArrayList<DataTable>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transfer_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tableType: TextView = view.findViewById(R.id.txtTableTypeTransfer)
        val tableNum: TextView = view.findViewById(R.id.txtTableNum)
        tableType.text = tableTypes.text.toString()
        tableNum.text = tableNums.text.toString()
        val tableTypeSpinner: Spinner = view.findViewById(R.id.dropdown_Type)
        val tableStatusSpinner: Spinner = view.findViewById(R.id.statusDropDown)
        val btnBack: ImageButton = view.findViewById(R.id.btnBackTransfer)

        btnBack.setOnClickListener{
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SelectTableFragment())
                .commitNow()
        }
        val tableTypeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Type,
            R.layout.spinner_status_design
        )
        val tableStatusAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Availability,
            R.layout.spinner_status_design
        )
        tableStatusAdapter.setDropDownViewResource(R.layout.spinner_status_design)
        tableTypeAdapter.setDropDownViewResource(R.layout.spinner_status_design)


        tableStatusSpinner.adapter = tableStatusAdapter
        tableStatusSpinner.setSelection(0)

        tableTypeSpinner.adapter = tableTypeAdapter

        val popup = PopupWindow(context)

        popup.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        val layoutManager = LinearLayoutManager(context)
        tableRecyclerView = view.findViewById(R.id.transferTableList)
        tableRecyclerView.layoutManager = layoutManager
        tableRecyclerView.setHasFixedSize(true)
        tableArrayList = arrayListOf()

        var status: Boolean? = null
        var type: String? = null
        tableStatusSpinner.onItemSelectedListener = (object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var selectedType:String = tableStatusSpinner.selectedItem.toString()

                if(selectedType == "All"){
                    status = null
                    getTableList(type, status)
                }
                if(selectedType =="Available"){
                    status=true
                    getTableList(type, status)
                }
                if(selectedType =="Occupied"){
                    status=false
                    getTableList(type, status)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        })
        tableTypeSpinner.onItemSelectedListener = (object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var selectedType:String = tableTypeSpinner.selectedItem.toString()

                if(selectedType == "All"){
                    type=null
                    getTableList(type, status)
                }
                if(selectedType =="Table"){
                    type="Table"
                    getTableList(type, status)
                }
                if(selectedType =="Hut"){
                    type="Hut"
                    getTableList(type, status)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun getTableList(selectedType: String?, status: Boolean?) {

        dbRef = FirebaseDatabase.getInstance().getReference("Dine").child("Tables")
        dbRef.addValueEventListener(object : ValueEventListener/*,
            tableAdapter.OnItemClickListener */ {
            override fun onDataChange(p0: DataSnapshot) {
                tableArrayList.clear()
                if (p0.exists()) {
                    for (tableSnapshot in p0.children) {
                        val table = tableSnapshot.getValue(DataTable::class.java)

                        if(selectedType.equals(null) && status==null){
                            tableArrayList.add(table!!)
                            for(i in tableArrayList){
                                if(i.id == Integer.parseInt(tableNums.text.toString())){
                                    tableArrayList.remove(i)
                                }
                            }
                        }
                        if((selectedType.equals(null) && table!!.Status == status) ||
                            (table!!.Category.equals(selectedType) && status == null)){
                            tableArrayList.add(table)
                            for(i in tableArrayList){
                                if(i.id == Integer.parseInt(tableNums.text.toString())){
                                    tableArrayList.remove(i)
                                }
                            }
                        }
                        if(table.Category.equals(selectedType) && table.Status== status){
                            tableArrayList.add(table)
                            for(i in tableArrayList){
                                if(i.id == Integer.parseInt(tableNums.text.toString())){
                                    tableArrayList.remove(i)
                                }
                            }
                        }


                    }

                }


                tableRecyclerView.adapter = TransferTableAdapter(tableArrayList,tableNums/*,this*/)

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
}