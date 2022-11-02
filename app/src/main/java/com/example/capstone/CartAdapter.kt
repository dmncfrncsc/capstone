package com.example.capstone

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import java.text.DecimalFormat


class CartAdapter(
    private val cartList: ArrayList<DataCartList>,
    private val txtTotal: TextView,
    private val btnCancel: Button,
    private val tableType: TextView,
    private val tableNum: TextView,
    private val btnConfirmOrders: Button,
    private val currentActivity: Any?
) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>() {
    private lateinit var tableData: ArrayList<DataTable>
    private lateinit var mealQuantity: ArrayList<DataMeal>
    private lateinit var beverages: ArrayList<DataBeverages>
    private val dbRef = FirebaseFirestore.getInstance().collection("orders")
    private val dbref2 = FirebaseDatabase.getInstance().getReference("Dine").child("Tables")
    private val dbref3 = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        tableData = arrayListOf()
        mealQuantity = arrayListOf()
        beverages = arrayListOf()
        val currentItem = cartList[position]
        val dec = DecimalFormat("0.00")
        var price: Long = 0
        holder.txtQty.text = currentItem.quantity.toString()
        holder.txtItemName.text = currentItem.ItemName.toString()
        holder.txtPrice.text = dec.format(currentItem.price.toString().toDouble())
        holder.subtotal.text = dec.format(currentItem.subTotal.toString().toDouble())
        val currentItemCode = currentItem.ItemCode
        val currentBucket = currentItem.isBucket

        if (currentItem.ImageUrl.isNotEmpty()) {
            Picasso.with(holder.itemView.context).load(currentItem.ImageUrl).fit().into(holder.orderImage)

        }else{

        }

        for (o in cartList) {
            price += o.subTotal
        }
        if(holder.txtAvailable.text.toString() == ""){
            getMeals(currentItemCode, holder)
            getBeverages(currentItemCode, holder)
        }

        txtTotal.text = dec.format(price)


        holder.btnAdd.setOnClickListener {
            var currentQty = Integer.parseInt(holder.txtQty.text.toString())
            var discount = currentQty * 20
            if (currentItem.category.equals("beverages", ignoreCase = true)) {

                if (currentItem.isBucket) {
                    val condition =Integer.parseInt(holder.txtAvailable.text.toString()) - 6
                    var newprice: Long = 0
                    if (condition > 1  ) {
                        for (i in cartList) {
                            if (i.ItemName.toString() == holder.txtItemName.text) {
                                val dbRefs = FirebaseFirestore.getInstance()

                                dbRefs.collection("beverages")
                                    .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                    .addOnSuccessListener { result ->
                                        for (docs in result) {
                                            docs.reference.update(
                                                "Quantity",
                                                Integer.parseInt(holder.txtAvailable.text.toString()) - 6
                                            )

                                            currentQty += 1
                                            holder.txtQty.text = currentQty.toString()
                                            i.addQty(currentQty)
                                            holder.subtotal.text =
                                                dec.format(Integer.parseInt(i.subTotal.toString()))
                                            for (o in cartList) {
                                                newprice += o.subTotal
                                            }
                                            txtTotal.text = dec.format(newprice)
                                        }
                                    }

                            }
                        }

                    }else{
                        Toast.makeText(holder.itemView.context, "Maximum quantity is reached", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    var newprice: Long = 0
                    if (Integer.parseInt(holder.txtAvailable.text.toString()) != 0) {

                        for (i in cartList) {
                            if (i.ItemName.toString() == holder.txtItemName.text) {

                                val dbRefs = FirebaseFirestore.getInstance()

                                dbRefs.collection("beverages")
                                    .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                    .addOnSuccessListener { result ->
                                        for (docs in result) {
                                            docs.reference.update(
                                                "Quantity",
                                                Integer.parseInt(holder.txtAvailable.text.toString()) - 1
                                            )
                                            currentQty += 1
                                            holder.txtQty.text = currentQty.toString()
                                            i.addQty(currentQty)
                                            holder.subtotal.text =
                                                dec.format(Integer.parseInt(i.subTotal.toString()))
                                            for (o in cartList) {
                                                newprice += o.subTotal
                                            }
                                            txtTotal.text = dec.format(newprice)
                                        }
                                    }
                            }
                        }
                    }
                    else{
                        Toast.makeText(holder.itemView.context, "Maximum quantity is reached", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            if (currentItem.category.equals("meal", ignoreCase = true)) {
                var newprice: Long = 0
                if (Integer.parseInt(holder.txtAvailable.text.toString()) != 0) {


                    for (i in cartList) {
                        if (i.ItemName.toString() == holder.txtItemName.text) {

                            val dbRefs = FirebaseFirestore.getInstance()

                            dbRefs.collection("meals")
                                .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                .addOnSuccessListener { result ->
                                    for (docs in result) {
                                        docs.reference.update(
                                            "Serving",
                                            Integer.parseInt(holder.txtAvailable.text.toString()) - 1

                                        )

                                        currentQty += 1
                                        holder.txtQty.text = currentQty.toString()
                                        holder.subtotal.text = dec.format(Integer.parseInt(i.subTotal.toString()))
                                        i.addQty(currentQty)
                                        getMeals(currentItem.ItemCode, holder)
                                        for (o in cartList) {
                                            newprice += o.subTotal
                                        }
                                        txtTotal.text = dec.format(newprice)

                                    }
                                }
                        }
                    }

                }else{
                    Toast.makeText(holder.itemView.context, "Maximum quantity is reached", Toast.LENGTH_SHORT).show()
                }
            }

            if(currentItem.category.equals("misc", ignoreCase = true)){
                var newprice: Long = 0
                currentQty += 1
                for(i in cartList){
                    if (i.ItemName.toString() == holder.txtItemName.text) {
                        var newprice: Long = (i.price * currentQty).toLong()
                        holder.txtQty.text = currentQty.toString()
                        holder.subtotal.text = dec.format(newprice)
                        i.addQty(currentQty)
                    }

                }
                for (o in cartList) {
                    newprice += o.subTotal
                }
                txtTotal.text = dec.format(newprice)
            }



        }
        holder.btnSub.setOnClickListener {
            var currentQty = Integer.parseInt(holder.txtQty.text.toString())
            var newprice: Long = 0

            if (currentItem.category.equals("beverages", ignoreCase = true)){

                if(currentItem.isBucket){
                    if (Integer.parseInt(holder.txtQty.text.toString()) > 1) {
                        for (i in cartList) {
                            if (i.ItemName.toString() == holder.txtItemName.text) {
                                val dbRefs = FirebaseFirestore.getInstance()

                                dbRefs.collection("beverages")
                                    .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                    .addOnSuccessListener { result ->
                                        for (docs in result) {
                                            docs.reference.update(
                                                "Quantity",
                                                Integer.parseInt(holder.txtAvailable.text.toString()) + 6
                                            )
                                            for (o in cartList) {
                                                newprice += o.subTotal
                                            }
                                            txtTotal.text = dec.format(newprice)
                                            currentQty -= 1
                                            i.subQty(currentQty)
                                            holder.txtQty.text = currentQty.toString()
                                            holder.subtotal.text =
                                                dec.format(Integer.parseInt(i.subTotal.toString()))
                                        }
                                    }

                            }
                        }

                    }
                }

                else{
                    var newprice: Long = 0
                    if (Integer.parseInt(holder.txtQty.text.toString()) > 1) {
                        for (i in cartList) {
                            if (i.ItemName.toString() == holder.txtItemName.text) {

                                val dbRefs = FirebaseFirestore.getInstance()

                                dbRefs.collection("beverages")
                                    .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                    .addOnSuccessListener { result ->
                                        for (docs in result) {
                                            docs.reference.update(
                                                "Quantity",
                                                Integer.parseInt(holder.txtAvailable.text.toString()) + 1
                                            )
                                            for (o in cartList) {
                                                newprice += o.subTotal
                                            }
                                            txtTotal.text = dec.format(newprice)
                                            currentQty -= 1
                                            i.subQty(currentQty)
                                            holder.txtQty.text = currentQty.toString()
                                            holder.subtotal.text =
                                                dec.format(Integer.parseInt(i.subTotal.toString()))
                                        }
                                    }
                            }
                        }

                    }
                }
            }

            if (currentItem.category.equals("meal", ignoreCase = true)){
                if (Integer.parseInt(holder.txtQty.text.toString()) > 1) {

                    for (i in cartList) {
                        if (i.ItemName.toString() == holder.txtItemName.text) {
                            val dbRefs = FirebaseFirestore.getInstance()

                            dbRefs.collection("meals")
                                .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                .addOnSuccessListener { result ->
                                    for (docs in result) {
                                        docs.reference.update(
                                            "Serving",
                                            Integer.parseInt(holder.txtAvailable.text.toString()) + 1

                                        )

                                        for (o in cartList) {
                                            newprice += o.subTotal
                                        }
                                        txtTotal.text = dec.format(newprice)
                                        holder.txtQty.text = currentQty.toString()
                                        holder.subtotal.text = dec.format(Integer.parseInt(i.subTotal.toString()))

                                        getMeals(currentItem.ItemCode, holder)

                                    }
                                }
                        }
                    }

                }
            }


            if(currentItem.category.equals("misc", ignoreCase = true)){
                if(Integer.parseInt(holder.txtQty.text.toString()) > 1){
                    var newpricess: Long = 0
                    for(i in cartList){
                        if (i.ItemName.toString() == holder.txtItemName.text) {
                            currentQty -= 1
                            i.subQty(currentQty)
                            holder.txtQty.text = currentQty.toString()
                            holder.subtotal.text = dec.format(Integer.parseInt(i.subTotal.toString()))
                        }

                    }
                    for (o in cartList) {
                        newpricess += o.subTotal
                    }
                    txtTotal.text = dec.format(newpricess)
                }

            }

        }

        holder.btnRemove.setOnClickListener {
            var newprice: Long = 0
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {

                            if (currentItem.category.equals("beverages", ignoreCase = true)){
                                if(currentItem.isBucket){
                                    for (i in cartList) {
                                        if (i.ItemName.toString() == holder.txtItemName.text) {
                                            val dbRefs = FirebaseFirestore.getInstance()
                                            dbRefs.collection("beverages")
                                                .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                                .addOnSuccessListener { result ->
                                                    for (docs in result) {
                                                        docs.reference.update(
                                                            "Quantity",
                                                            (Integer.parseInt(holder.txtAvailable.text.toString()) + (Integer.parseInt(holder.txtQty.text.toString())*6))
                                                        ).addOnFailureListener {e->
                                                            Log.d("Failed", "${e}Failed")
                                                        }
                                                        cartList.remove(i)
                                                        notifyItemRemoved(position)
                                                        for (o in cartList) {
                                                            newprice += o.subTotal
                                                        }
                                                        txtTotal.text = dec.format(newprice)
                                                        break;
                                                    }
                                                }

                                            Toast.makeText(holder.itemView.context, "${currentItem.ItemName} successfully removed.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                else{
                                    for (i in cartList) {
                                        if (i.ItemName.toString() == holder.txtItemName.text) {
                                            val dbRefs = FirebaseFirestore.getInstance()
                                            dbRefs.collection("beverages")
                                                .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                                .addOnSuccessListener { result ->
                                                    for (docs in result) {
                                                        docs.reference.update(
                                                            "Quantity",
                                                            (Integer.parseInt(holder.txtAvailable.text.toString()) + Integer.parseInt(holder.txtQty.text.toString()))
                                                        ).addOnFailureListener {e->
                                                            Log.d("Failed", "${e}Failed")
                                                        }
                                                        cartList.remove(i)
                                                        notifyItemRemoved(position)
                                                        for (o in cartList) {
                                                            newprice += o.subTotal
                                                        }
                                                        txtTotal.text = dec.format(newprice)
                                                        Toast.makeText(holder.itemView.context, "${currentItem.ItemName} successfully removed.", Toast.LENGTH_SHORT).show()
                                                        break;

                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                            if (currentItem.category.equals("meal", ignoreCase = true)){
                                for (i in cartList) {
                                    if (i.ItemName.toString() == holder.txtItemName.text) {
                                        val dbRefs = FirebaseFirestore.getInstance()
                                        dbRefs.collection("meals")
                                            .whereEqualTo("ItemCode", currentItem.ItemCode).get()
                                            .addOnSuccessListener { result ->
                                                for (docs in result) {
                                                    docs.reference.update(
                                                        "Serving",
                                                        (Integer.parseInt(holder.txtAvailable.text.toString()) + Integer.parseInt(holder.txtQty.text.toString()))
                                                    ).addOnFailureListener {e->
                                                        Log.d("Failed", "${e}Failed")
                                                    }
                                                    cartList.remove(i)
                                                    notifyItemRemoved(position)
                                                    for (o in cartList) {
                                                        newprice += o.subTotal
                                                    }
                                                    Toast.makeText(holder.itemView.context, "${currentItem.ItemName} successfully removed.", Toast.LENGTH_SHORT).show()
                                                    txtTotal.text = dec.format(newprice)
                                                    break;
                                                }
                                            }
                                    }
                                }

                            }

                            if(currentItem.category.equals("misc", ignoreCase = true)){
                                for (i in cartList) {
                                    if (i.ItemName.toString() == holder.txtItemName.text) {
                                        cartList.remove(i)
                                        notifyItemRemoved(position)
                                        for (o in cartList) {
                                            newprice += o.subTotal
                                        }
                                        Toast.makeText(holder.itemView.context, "${currentItem.ItemName} successfully removed.", Toast.LENGTH_SHORT).show()
                                        txtTotal.text = dec.format(newprice)
                                    }
                                }
                            }
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {}

                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
            builder.setMessage("Remove item ${currentItem.ItemName.toString()} to this list?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }
        btnCancel.setOnClickListener {
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container, TakeOrderFragment(
                        tableNum,
                        tableType,
                        cartList,
                        currentActivity
                    )
                )
                .commitNow()
        }

        btnConfirmOrders.setOnClickListener {
            getTableData()
            val dbReference =
                dbRef.whereEqualTo("tableId", Integer.parseInt(tableNum.text.toString()))
            val tsLong = System.currentTimeMillis() / 1000
            val ts = tsLong.toString()
            val queue: DataQueue = DataQueue()

            queue.CreateTableId(Integer.parseInt(tableNum.text.toString()))
            queue.CreateTimeStamp(ts)

            val db = FirebaseFirestore.getInstance()
            val docData = hashMapOf(
                "tableId" to queue.tableId,
                "timeStamp" to queue.timeStamp.toString().toLong(),
                "status" to "preparing"
            )
            val ref: DocumentReference = db.collection("orderQueue").document()
            val docId = ref.id

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        updateTableData()
                        db.collection("orderQueue").document(docId).set(docData)
                        for (o in cartList) {

                            if(o.category.equals("meal",ignoreCase = true) || o.category.equals("beverages",ignoreCase = true)){
                                val cart = DataOrders(
                                    o.TableId,
                                    o.ItemName,
                                    o.price,
                                    o.quantity,
                                    o.subTotal,
                                    docId,
                                    false,
                                    "order",
                                    o.ImageUrl,
                                    o.isBucket
                                )
                                dbRef.document().set(cart)
                            }
                            else{
                                val cart = DataOrders(
                                    o.TableId,
                                    o.ItemName,
                                    o.price,
                                    o.quantity,
                                    o.subTotal,
                                    docId,
                                    false,
                                    "misc",
                                    o.ImageUrl,
                                    o.isBucket
                                )
                                dbRef.document().set(cart)
                            }


                        }

                        cartList.clear()
                        val activity = it.context as AppCompatActivity
                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, OrdersCompleteFragment())
                            .commitNow()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {}

                }
            }
            val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
            val show = builder.setMessage("Confirm Orders?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()


//            if (currentActivity!!.javaClass.toString() == OrderListFragment(
//                    tableType,
//                    tableNum
//                ).javaClass.toString()
//            ){
//
//            }
        }
        /*    btnConfirmOrders.setOnClickListener {
                val dbRef3 = dbRef.whereEqualTo("tableId", Integer.parseInt(tableNum.text.toString()))
                val dbArrayList: ArrayList<DataOrders> = arrayListOf()
                if (currentActivity!!.javaClass.toString() == OrderListFragment(
                        tableType,
                        tableNum
                    ).javaClass.toString()
                ) {
                    dbRef3.get().addOnSuccessListener { result ->
                        for (document in result) {

                            dbArrayList.add(document.toObject(DataOrders::class.java))
                        }

                        for (o in cartList) {
                            dbRef3.whereEqualTo("itemName", o.ItemName.toString())
                                .get()
                                .addOnSuccessListener { results ->
                                    if (results.isEmpty) {
                                        val cart = DataCartList(
                                            o.TableId,
                                            o.ItemName,
                                            o.price,
                                            o.quantity,
                                            o.subTotal,
                                            o.status
                                        )
                                        dbRef.document().set(cart)

                                    }
                                    else {
                                        for (document in results) {
                                            var qty= 0;
                                           for(x in dbArrayList){
                                               qty = Integer.parseInt(x.quantity.toString())
                                           }
                                            document.reference.update("quantity", (qty+o.quantity))
                                            document.reference.update("subTotal", (qty+o.quantity)*o.price)
                                        }
                                    }

                                    Toast.makeText(
                                        holder.itemView.context,
                                        "Successfully placed order",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    cartList.clear()
                                    val activity = it.context as AppCompatActivity
                                    activity.supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, currentActivity as Fragment)
                                        .commitNow()
                                }
                        }
                    }

                } else {


                        }

                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setMessage("Confirm Orders?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show()
                }
            }*/




        /* if (currentItem.category.equals("beverages", ignoreCase = true)) {
             dbref3.collection("beverages").whereEqualTo("ItemCode", currentItem.ItemCode).get()
                 .addOnSuccessListener { result ->
                     for (docs in result) {
                         val data = docs.toObject(DataBeverages::class.java)
                         if (data.ItemCode == currentItem.ItemCode) {
                             holder.txtAvailable.text = data.Quantity.toString()
                             notifyItemChanged(position)
                         }

                     }
                 }

         }*/


    }

    private fun getBeverages(
        currentItemCode: Any,
        holder: MyViewHolder,


    ) {
        beverages.clear()
        dbref3.collection("beverages").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    val data = dc.document.toObject(DataBeverages::class.java)
                    beverages.add(data)

                    for (o in beverages) {
                        if (o.ItemCode.toString()
                                .equals(currentItemCode.toString(), ignoreCase = true)
                        ) {
                            holder.txtAvailable.text = o.Quantity.toString()

                        }
                    }
                }
            }

        })
    }

    private fun getMeals(currentItemCode: Any, holder: MyViewHolder) {

        mealQuantity.clear()
        dbref3.collection("meals").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    val data = dc.document.toObject(DataMeal::class.java)
                    mealQuantity.add(data)

                    for (o in mealQuantity) {
                        if (o.ItemCode.toString()
                                .equals(currentItemCode.toString(), ignoreCase = true)
                        ) {

                            holder.txtAvailable.text = o.Serving.toString()

                        }
                    }
                }
            }

        })
    }

    private fun updateTableData() {

        for (i in tableData) {
            if (i.id.toString() == tableNum.text.toString()) {
                dbref2.child(i.id.toString()).child("Status").setValue(false)
                dbref2.child(i.id.toString()).child("Color").setValue("red")
            }
        }


    }

    private fun getTableData() {

        dbref2.addValueEventListener(object : ValueEventListener/*,
            tableAdapter.OnItemClickListener */ {
            override fun onDataChange(p0: DataSnapshot) {
                tableData.clear()
                if (p0.exists()) {
                    for (tableSnapshot in p0.children) {
                        val table = tableSnapshot.getValue(DataTable::class.java)
                        tableData.add(table!!)
                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }


    override fun getItemCount(): Int {
        return cartList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtItemName: TextView = itemView.findViewById(R.id.txtItemName)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val btnAdd: ImageButton = itemView.findViewById(R.id.btnAddQty)
        val btnSub: ImageButton = itemView.findViewById(R.id.btnSubQty)
        val txtQty: TextView = itemView.findViewById(R.id.txtQty)
        val subtotal: TextView = itemView.findViewById(R.id.txtSubtotal)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
        val txtAvailable: TextView = itemView.findViewById(R.id.txtRemainingQty)
        val orderImage: ImageView = itemView.findViewById(R.id.orderImage)
    }

}