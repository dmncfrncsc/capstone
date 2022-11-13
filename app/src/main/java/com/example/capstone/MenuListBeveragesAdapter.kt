package com.example.capstone

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class MenuListBeveragesAdapter(
    private val menuList: ArrayList<DataBeverages>,
    private val tableType: TextView, private val tableNum: TextView,
    private val orderList: ArrayList<DataCartList>,
    private val btnViewOrderList: ImageButton,
    private val currentActivity: Any?
) :
    RecyclerView.Adapter<MenuListBeveragesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.take_order_menu_items, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (orderList.size > 0) {
            btnViewOrderList.isEnabled = true
            btnViewOrderList.setBackgroundResource(R.drawable.floating_button_background)
        } else {
            btnViewOrderList.isEnabled = false
            btnViewOrderList.setBackgroundResource(R.drawable.floating_button_background_disabled)
        }
        val current = menuList[position]
        val numQty = Integer.parseInt(current.Quantity.toString())
        holder.itemView.setBackgroundResource(R.drawable.take_order_menu_bg)
        holder.linearCategory.visibility = if (current.Bucket!!) View.VISIBLE else View.GONE


        if(numQty == 0){
            holder.linearCategory.visibility = View.GONE
            holder.itemView.setBackgroundResource(R.drawable.out_of_stock)
            holder.expandOption.visibility = View.GONE
            holder.tvWarning.visibility= View.VISIBLE
            holder.tvWarning.text = "OUT OF STOCK"
            holder.tvWarning.setTextColor(Color.BLACK);
        }else if(numQty < 5){
            holder.tvWarning.visibility= View.VISIBLE
            holder.tvWarning.text = "STOCK IS LOW"
            holder.itemView.setBackgroundResource(R.drawable.low_stock_)
        }else{
            holder.tvWarning.visibility= View.GONE
            holder.itemView.setBackgroundResource(R.drawable.take_order_menu_bg)
        }


        if (current.ImageUrl.toString() != "null") {
            Picasso.with(holder.itemView.context).load(current.ImageUrl).fit().into(holder.orderImage)
        }
        if (current.isBucketSelected) {
            holder.btnBucket.isEnabled = false
            holder.btnBucket.setBackgroundResource(R.drawable.btn_disabled_bg)
        } else {
            holder.btnBucket.isEnabled = true
            holder.btnBucket.setBackgroundResource(R.drawable.btncart_confirm_bg)
        }
        if (current.isSoloSelected) {
            holder.btnSolo.isEnabled = false
            holder.btnSolo.setBackgroundResource(R.drawable.btn_disabled_bg)
        } else {
            holder.btnSolo.isEnabled = true
            holder.btnSolo.setBackgroundResource(R.drawable.btncart_confirm_bg)
        }
        holder.btnBucket.setOnClickListener {

            if (current.Quantity!! < 6) {
                Toast.makeText(
                    holder.itemView.context,
                    "Not Enough Quantity",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                for (o in menuList) {
                    if (o.BeverageName == current.BeverageName) {
                        o.isBucket(true)
                        o.isSolo(false)
                        o.newOldPrice(o.Price!!)
                        o.newOldQty(o.Quantity!!)
                        o.editPrice((o.Price!! * 6)-20)
                        o.editQuantity(o.Quantity!! / 6)
                    }
                }
                notifyItemChanged(position)
            }
        }
        holder.btnSolo.setOnClickListener {
            for (o in menuList) {
                if (o.BeverageName == current.BeverageName) {
                    if (o.oldPrice != null) {
                        o.editPrice(o.oldPrice!!)
                        o.editQuantity(o.oldQuantity!!)
                    }
                    o.isBucket(false)
                    o.isSolo(true)
                }
            }
           notifyItemChanged(position)
        }
        holder.txtQty.text = "0"
        var extraName: String? = null
        var qty = Integer.parseInt(holder.txtQty.text.toString())
        if (current.Size != null && current.Details.toString() != "") {
            extraName = current.Size.toString() + current.Details.toString()
        } else {
            extraName = ""
        }
        holder.itemName.text =
            "${current.BeverageName.toString()} $extraName"
        holder.price.text = current.Price.toString()
        holder.stock.text = current.Quantity.toString()
        holder.btnAddCart.isEnabled = (Integer.parseInt(holder.txtQty.text.toString()) <= 0)
        fun checkExistingItem(name: String): Boolean {
            for (i in orderList) {
                if (i.ItemName == name) {
                    return true
                }
            }
            return false
        }
        btnViewOrderList.setOnClickListener {
            if (btnViewOrderList.isEnabled) {
                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container, CartFragment(
                            orderList,
                            tableType,
                            tableNum,
                            currentActivity
                        )
                    ).commitNow()
            }
        }
        holder.btnAddQty.setOnClickListener {
            if (current.Bucket) {
                if (!current.isSoloSelected && !current.isBucketSelected) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Please select variation",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if(current.Quantity!! !=0){
                        if (current.Quantity!! > Integer.parseInt(holder.txtQty.text.toString())) {
                            qty += 1
                            holder.txtQty.text = qty.toString()
                            holder.stock.text = (current.Quantity!! - qty).toString()
                        }else{
                            Toast.makeText(holder.itemView.context, "Maximum quantity is reached", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(holder.itemView.context, "Maximum quantity is reached", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                if(current.Quantity!! !=0){
                    if (current.Quantity!! > Integer.parseInt(holder.txtQty.text.toString())) {
                        qty += 1
                        holder.txtQty.text = qty.toString()
                        holder.stock.text = (current.Quantity!! - qty).toString()
                    }else{
                        Toast.makeText(holder.itemView.context, "Maximum quantity is reached", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(holder.itemView.context, "Maximum quantity is reached", Toast.LENGTH_SHORT).show()
                }
            }
        }
        holder.btnSubtract.setOnClickListener {
            if (qty > 0) {
                qty -= 1
                holder.txtQty.text = qty.toString()
                holder.stock.text = (current.Quantity!! - qty).toString()
            }
        }
        holder.btnAddCart.setOnClickListener {
            var imageUrl: String = ""
            imageUrl = if(current.ImageUrl == null || current.ImageUrl == ""){
                ""
            }else{
                current.ImageUrl!!
            }
            val dbRef = FirebaseFirestore.getInstance()
            val txtNum = Integer.parseInt(tableNum.text.toString())
            val price = current.Price.toString().toLong()
            val itemName = current.BeverageName.toString() +" "+ extraName
            val qtys = Integer.parseInt(holder.txtQty.text.toString())
            val subtotal: Long = price * qtys
            val status = false
            if (current.Bucket) {
                if (!current.isSoloSelected && !current.isBucketSelected) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Please select variation.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (qtys == 0) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Quantity cannot be zero (0).",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    if (current.isSoloSelected) {
                        val dbRef = FirebaseFirestore.getInstance()
                        val soloName = "S. $itemName"
                        if (checkExistingItem(soloName)) {
                            for (o in orderList) {
                                if (o.ItemName == soloName) {
                                    o.editQty(qtys)
                                    dbRef.collection("beverages")
                                        .whereEqualTo("BeverageName", current.BeverageName).get()
                                        .addOnSuccessListener { result ->
                                            for (docs in result) {
                                                docs.reference.update(
                                                    "Quantity",
                                                    current.Quantity!! - qtys
                                                )
                                            }
                                        }
                                    Toast.makeText(holder.itemView.context, "$soloName added.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            orderList.add(
                                DataCartList(
                                    current.ItemCode!!,
                                    txtNum,
                                    soloName, price, qty, subtotal, "beverages", status, false,imageUrl
                                )
                            )
                            val dbRefs = FirebaseFirestore.getInstance()
                            dbRefs.collection("beverages")
                                .whereEqualTo("BeverageName", current.BeverageName).get()
                                .addOnSuccessListener { result ->
                                    for (docs in result) {
                                        docs.reference.update("Quantity", current.Quantity!! - qtys)
                                    }
                                }
                            Toast.makeText(holder.itemView.context, "$soloName added.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (current.isBucketSelected) {
                        val dbRef = FirebaseFirestore.getInstance()
                        val bucketName = "B. $itemName"
                        val bucketQuantity = 6 * qtys
                        if (checkExistingItem(bucketName)) {
                            for (o in orderList) {
                                if (o.ItemName == bucketName) {
                                    o.editQty(qtys)
                                    dbRef.collection("beverages")
                                        .whereEqualTo("BeverageName", current.BeverageName).get()
                                        .addOnSuccessListener { result ->
                                            for (docs in result) {
                                                docs.reference.update(
                                                    "Quantity",
                                                    current.oldQuantity!! - bucketQuantity
                                                )
                                            }
                                        }
                                }
                            }
                        } else {
                            orderList.add(
                                DataCartList(
                                    current.ItemCode!!,
                                    txtNum,
                                    bucketName, price, qty, subtotal, "beverages", status, true,imageUrl
                                )
                            )
                            val dbRef = FirebaseFirestore.getInstance()
                            dbRef.collection("beverages")
                                .whereEqualTo("BeverageName", current.BeverageName).get()
                                .addOnSuccessListener { result ->
                                    for (docs in result) {
                                        docs.reference.update(
                                            "Quantity",
                                            current.oldQuantity!! - bucketQuantity
                                        )
                                    }
                                }
                        }
                    }
                }
            }
            else {
                if (qtys == 0) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Quantity cannot be zero (0).",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (checkExistingItem(itemName)) {
                        for (o in orderList) {
                            if (o.ItemName == itemName) {
                                o.editQty(qtys)
                                dbRef.collection("beverages")
                                    .whereEqualTo("BeverageName", current.BeverageName).get()
                                    .addOnSuccessListener { result ->
                                        for (docs in result) {
                                            docs.reference.update(
                                                "Quantity",
                                                current.Quantity!! - qtys
                                            )
                                        }
                                    }
                                Toast.makeText(holder.itemView.context, "${current.BeverageName} added.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        orderList.add(
                            DataCartList(
                                current.ItemCode!!,
                                txtNum,
                                itemName, price, qty, subtotal, "beverages", status, false, imageUrl
                            )
                        )
                        dbRef.collection("beverages")
                            .whereEqualTo("BeverageName", current.BeverageName).get()
                            .addOnSuccessListener { result ->
                                for (docs in result) {
                                    docs.reference.update("Quantity", current.Quantity!! - qtys)
                                }
                            }

                        Toast.makeText(holder.itemView.context, "${current.BeverageName} added.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val stock: TextView = itemView.findViewById(R.id.txtRemainingStock)
        val expandOption: ConstraintLayout = itemView.findViewById(R.id.expand)
        val linear: LinearLayout = itemView.findViewById(R.id.linearTop)
        val txtQty: TextView = itemView.findViewById(R.id.txtQty)
        val btnAddQty: ImageButton = itemView.findViewById(R.id.btnAddQty)
        val btnSubtract: ImageButton = itemView.findViewById(R.id.btnSubQty)
        val btnAddCart: Button = itemView.findViewById(R.id.btnAddToCart)
        val linearCategory: LinearLayout = itemView.findViewById(R.id.linearCategory)
        val btnSolo: Button = itemView.findViewById(R.id.btnSolo)
        val btnBucket: Button = itemView.findViewById(R.id.btnBucket)
        val orderImage: ImageView = itemView.findViewById(R.id.orderImage)
        val linearLay: LinearLayout = itemView.findViewById(R.id.linearLay)
        val tvWarning: TextView = itemView.findViewById(R.id.tvWarning)
    }
}