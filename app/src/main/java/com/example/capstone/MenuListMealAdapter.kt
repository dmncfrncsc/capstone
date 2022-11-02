package com.example.capstone

import android.graphics.Color
import android.opengl.Visibility
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
import java.text.DecimalFormat

class MenuListMealAdapter(
    private val menuList: ArrayList<DataMeal>,
    private val tableType: TextView,
    private val tableNum: TextView,
    private val orderList: ArrayList<DataCartList>,
    private val btnViewOrderList: ImageButton,
    private val currentActivity: Any?
) :
    RecyclerView.Adapter<MenuListMealAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.take_order_menu_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if(orderList.size >0){
            btnViewOrderList.isEnabled = true
            btnViewOrderList.setBackgroundResource(R.drawable.floating_button_background)
        }else{
            btnViewOrderList.isEnabled = false
            btnViewOrderList.setBackgroundResource(R.drawable.floating_button_background_disabled)
        }
        val currentItem = menuList[position]

        val servings = Integer.parseInt(currentItem.Serving.toString())


        if(servings == 0){
            holder.linearCategory.visibility = View.GONE
            holder.itemView.setBackgroundResource(R.drawable.out_of_stock)
            holder.expandOption.visibility = View.GONE
            holder.tvWarning.visibility= View.VISIBLE
            holder.tvWarning.text = "OUT OF STOCK"
            holder.tvWarning.setTextColor(Color.BLACK);
        }else if(servings < 5){
            holder.linearCategory.visibility = View.GONE
            holder.tvWarning.visibility= View.VISIBLE
            holder.tvWarning.text = "STOCK IS LOW"
            holder.itemView.setBackgroundResource(R.drawable.low_stock_)
        }else{
            holder.linearCategory.visibility = View.GONE
            holder.tvWarning.visibility= View.GONE
            holder.itemView.setBackgroundResource(R.drawable.take_order_menu_bg)
        }
        val dec = DecimalFormat("#,###.##")
        val txtTable = tableType.text.toString()
        val txtNum = Integer.parseInt(tableNum.text.toString())
        holder.txtQty.text = "0"
        var qty = Integer.parseInt(holder.txtQty.text.toString())
        holder.itemName.text = currentItem.MealName
        holder.price.text = currentItem.Price.toString()
        holder.stock.text = currentItem.Serving.toString()



        if (currentItem.ImageUrl.toString() != "null") {
            Picasso.with(holder.itemView.context).load(currentItem.ImageUrl).fit().into(holder.orderImage)

        }

        holder.btnAddQty.setOnClickListener {
            if (currentItem.Serving!! > Integer.parseInt(holder.txtQty.text.toString())) {
                qty += 1
                holder.txtQty.text = qty.toString()
                holder.stock.text = (currentItem.Serving!! - qty).toString()
            }
        }

        holder.btnSubtract.setOnClickListener {
            if (qty > 0) {
                qty -= 1
                holder.txtQty.text = qty.toString()
                holder.stock.text = (currentItem.Serving!! - qty).toString()
            }
        }




        fun checkExistingItem(name: String): Boolean {
            for (i in orderList) {
                if (i.ItemName == name) {
                    return true
                }
            }
            return false
        }

        holder.btnAddCart.setOnClickListener {
            var imageUrl: String = if(currentItem.ImageUrl == null || currentItem.ImageUrl == ""){
                ""
            }else{
                currentItem.ImageUrl!!
            }

            val dbRef = FirebaseFirestore.getInstance()
            val price = currentItem.Price.toString().toLong()
            val itemName = currentItem.MealName.toString()
            val qtys = Integer.parseInt(holder.txtQty.text.toString())
            val subtotal:Long =  price * qtys
            val status = false
            val category = "meal"
            if(qtys == 0){
                Toast.makeText(
                    holder.itemView.context,
                    "Quantity cannot be zero (0).",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                if(checkExistingItem(itemName)){
                    for(o in orderList){
                        if(o.ItemName == itemName){
                            o.editQty(qtys)
                            dbRef.collection("meals")
                                .whereEqualTo("MealName", currentItem.MealName).get()
                                .addOnSuccessListener { result ->
                                    for (docs in result) {
                                        docs.reference.update(
                                            "Serving",
                                            currentItem.Serving!! - qtys
                                        )
                                    }
                                }
                            Toast.makeText(holder.itemView.context, "${currentItem.MealName} added.", Toast.LENGTH_SHORT).show()

                        }

                    }

                  //  notifyItemChanged(position)

                }
                else{
                    orderList.add(
                        DataCartList(
                            currentItem.ItemCode!!,
                            txtNum,
                            itemName, price, qty, subtotal, "meal" ,status,false, imageUrl
                        )
                    )
                    dbRef.collection("meals")
                        .whereEqualTo("MealName", currentItem.MealName).get()
                        .addOnSuccessListener { result ->
                            for (docs in result) {
                                docs.reference.update(
                                    "Serving",
                                    currentItem.Serving!! - qtys
                                )
                            }
                        }

                    Toast.makeText(holder.itemView.context, "${currentItem.MealName} added.", Toast.LENGTH_SHORT).show()
                   // notifyItemChanged(position)
                }
            }



        }
        btnViewOrderList.setOnClickListener {
            if(btnViewOrderList.isEnabled){

                val activity = it.context as AppCompatActivity

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CartFragment(
                        orderList,
                        tableType,
                        tableNum,
                        currentActivity
                    )).commitNow()
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
        val orderImage: ImageView = itemView.findViewById(R.id.orderImage)
        val tvWarning: TextView = itemView.findViewById(R.id.tvWarning)
        val tvQ: TextView = itemView.findViewById(R.id.tvQ)
        val categorys: FrameLayout = itemView.findViewById(R.id.category)
    }
}