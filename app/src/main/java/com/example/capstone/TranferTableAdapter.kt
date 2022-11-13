package com.example.capstone

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class TransferTableAdapter(
    private val tableList: ArrayList<DataTable>,
    private val tableNums: TextView
) :
    RecyclerView.Adapter<TransferTableAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transfer_table_items, parent, false)
        return MyViewHolder(itemView)
    }

    private val dbRefs = FirebaseFirestore.getInstance().collection("orders")
    private val dbQueue = FirebaseFirestore.getInstance().collection("orderQueue")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = tableList[position]
        holder.tableTyp.text = currentItem.Category.toString()
        holder.tableNum.text = currentItem.id.toString()
        if (holder.tableTyp.text.equals("Door")) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
        if (holder.tableTyp.text.equals("Table")) {
            holder.iconType.setImageResource(R.drawable.icon_table)
        }
        if (holder.tableTyp.text.equals("Hut")) {
            holder.iconType.setImageResource(R.drawable.icon_hut)
        }
        if (!currentItem.Status!!) {
            holder.itemView.setBackgroundResource(R.drawable.table_occupied_bg)
            holder.tableTyp.setTextColor(Color.WHITE)
            holder.tableNum.setTextColor(Color.WHITE)
            holder.poundSign.setTextColor(Color.WHITE)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.table_items_bg)
            holder.tableTyp.setTextColor(Color.BLACK)
            holder.tableNum.setTextColor(Color.BLACK)
            holder.poundSign.setTextColor(Color.BLACK)
        }
        holder.btnTable.setOnClickListener {
            val tableArray: ArrayList<DataTable> = arrayListOf()
            val dbRef = FirebaseDatabase.getInstance().reference.child("Dine").child("Tables")
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        for (tableSnapshot in p0.children) {
                            val table = tableSnapshot.getValue(DataTable::class.java)
                            tableArray.add(table!!)
                        }
                        swapOrders()
                        swapTables(dbRef, tableArray)
                        val activity = it.context as AppCompatActivity
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, TransferTableCompleteFragment())
                            .commitNow()
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
                private fun swapOrders() {
                    val docId = DocumentIdData()
                    val dbArrayList: ArrayList<DataOrders> = arrayListOf()
                    val dbArrayList2: ArrayList<DataOrders> = arrayListOf()
                    val queueArrayList: ArrayList<DataSwapQueue> = arrayListOf()
                    val queueArrayList2: ArrayList<DataSwapQueue> = arrayListOf()
                    val item1 = Integer.parseInt(currentItem.id.toString())
                    val item2 = Integer.parseInt(tableNums.text.toString())
                    dbQueue.whereEqualTo("tableId", item2).get().addOnSuccessListener() {
                            task ->
                        for (document in task) {
                            val data = document.toObject(DataSwapQueue::class.java)
                            docId.documentId2 = document.id
                            queueArrayList.add(data)
                            document.reference.delete()
                            for(o in queueArrayList){
                                if (o.timeStamp == data.timeStamp){
                                    o.addQueueId(docId.documentId2!!)
                                }
                            }
                        }
                        for(o in queueArrayList){
                            if (item2 == o.tableId) {
                                o.swapTables(item1)
                            }
                        }
                        dbQueue.whereEqualTo("tableId", item1).get().addOnSuccessListener(){
                                task2 ->
                            for(document2 in task2){
                                val data = document2.toObject(DataSwapQueue::class.java)
                                docId.documentId1 = document2.id
                                queueArrayList2.add(data)
                                document2.reference.delete()
                                for(o in queueArrayList2){
                                    if (o.timeStamp == data.timeStamp){
                                        o.addQueueId(docId.documentId1!!)
                                    }
                                }
                            }
                            for(o in queueArrayList2){
                                if (item1 == o.tableId) {
                                    o.swapTables(item2)
                                }
                            }
                            queueArrayList.addAll(queueArrayList2)
                            for (i in queueArrayList) {
                                val swappedData = QueueSwap(
                                    i.tableId,
                                    i.timeStamp,
                                    i.status
                                )
                                if(i.timeStamp == swappedData.timeStamp){
                                    dbQueue.document(i.QueueId.toString()).set(swappedData)
                                }
                            }
                        }
                    }

                    //swap tables
                    dbRefs.whereEqualTo("tableId", item2).get().addOnSuccessListener { result1 ->
                        for (document in result1) {
                            dbArrayList.add(document.toObject(DataOrders::class.java))
                            document.reference.delete()
                        }
                        for (o in dbArrayList) {
                            if (item2 == o.tableId) {
                                o.swapTables(item1)
                            }
                        }
                        dbRefs.whereEqualTo("tableId", item1).get()
                            .addOnSuccessListener { results ->
                                for (docs in results) {
                                    dbArrayList2.add(docs.toObject(DataOrders::class.java))
                                    docs.reference.delete()
                                }
                                for (i in dbArrayList2) {
                                    if (item1 == i.tableId) {
                                        i.swapTables(item2)
                                    }
                                }
                                dbArrayList.addAll(dbArrayList2)
                                for (i in dbArrayList) {
                                    val swappedData = DataOrders(
                                        i.tableId,
                                        i.itemName,
                                        i.price,
                                        i.quantity,
                                        i.subTotal,
                                        i.queueID,
                                        i.status,
                                        i.category,
                                        i.imageUrl,
                                        i.isBucket
                                    )
                                    dbRefs.document().set(swappedData)
                                }
                            }
                    }
                }
                private fun swapTables(
                    dbRef: DatabaseReference,
                    tableArrays: ArrayList<DataTable>
                ) {
                    var status: Boolean? = null
                    var color: String? = null
                    var status2: Boolean? = null
                    var color2: String? = null
                    for (o in tableArrays) {
                        if (tableNums.text.equals(o.id.toString())) {
                            status = o.Status
                            color = o.Color
                        }
                    }
                    for (o in tableArrays) {
                        if (o.id.toString() == currentItem.id.toString()) {
                            status2 = o.Status
                            color2 = o.Color
                        }
                    }
                    for (o in tableArrays) {
                        if (tableNums.text.equals(o.id.toString())) {
                            o.editInfo(status2!!, color2!!)
                        }
                    }
                    for (o in tableArrays) {
                        if (o.id.toString() == currentItem.id.toString()) {
                            o.editInfo(status!!, color!!)
                        }
                    }
                    for (o in tableArrays) {
                        if (o.id.toString() == tableNums.text) {
                            dbRef.child(o.id.toString()).child("Status").setValue(o.Status)
                            dbRef.child(o.id.toString()).child("Color").setValue(o.Color)
                            dbRef.removeEventListener(this)
                        }
                    }
                    for (o in tableArrays) {
                        if (o.id.toString() == currentItem.id.toString()) {
                            dbRef.child(o.id.toString()).child("Status").setValue(o.Status)
                            dbRef.child(o.id.toString()).child("Color").setValue(o.Color)
                            dbRef.removeEventListener(this)
                        }
                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return tableList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tableTyp: TextView = itemView.findViewById(R.id.tvTableType)
        val tableNum: TextView = itemView.findViewById(R.id.tableNum)
        val iconType: ImageView = itemView.findViewById(R.id.iconType)
        val poundSign: TextView = itemView.findViewById(R.id.poundSign)
        val btnTable: LinearLayout = itemView.findViewById(R.id.btnTable)
    }
}