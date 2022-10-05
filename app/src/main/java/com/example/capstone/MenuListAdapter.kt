package com.example.capstone

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class MenuListAdapter(
    private val menuList: ArrayList<menu_meal>,
    private val tableType: TextView, private val tableNum: TextView,
    private val orderList: ArrayList<DataCartList>
) :
    RecyclerView.Adapter<MenuListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.take_order_menu_meal_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = menuList[position]
        val dec = DecimalFormat("#,###.##")
        val currentQuantity: Int;
        holder.txtQty.text = "1"
        var qty = Integer.parseInt(holder.txtQty.text.toString())

        holder.itemName.text = currentItem.MealName
        holder.price.text = dec.format(currentItem.Price).toString()
        holder.stock.text = currentItem.Status.toString()
        holder.itemView.setBackgroundResource(R.drawable.take_order_menu_bg)

        var isVisible: Boolean = currentItem.visibility
        holder.expandOption.visibility =
            if (isVisible) {
                View.VISIBLE
            } else View.GONE

        holder.linear.setOnClickListener {

            currentItem.visibility = !currentItem.visibility

            notifyItemChanged(position)

        }

        holder.btnAddQty.setOnClickListener {

            qty += 1

            holder.txtQty.text = qty.toString()

        }

        holder.btnSubtract.setOnClickListener {

            if (qty > 1) {
                qty -= 1
            }
            holder.txtQty.text = qty.toString()
        }


        fun editQty(name:String, qty:Int){

            for (o in orderList) {

                if (name == o.itemName) {
                    o.editQty(qty + o.qty)
                    return
                }
            }
        }

        fun checkExistingItem(name: String): Boolean {
            for (i in orderList) {
                if (i.itemName.equals(name)) {
                    return true
                }
            }
            return false
        }

        holder.btnAddCart.setOnClickListener {
            val txtTable = tableType.text.toString()
            val txtNum = Integer.parseInt(tableNum.text.toString())
            val price = currentItem.Price.toString().toLong()
            val itemName = currentItem.MealName.toString()
            val qty = Integer.parseInt(holder.txtQty.text.toString())
            val subtotal:Long =  price * qty

            if(checkExistingItem(itemName))
            {
                editQty(itemName,qty)
            }else{
                orderList.add(DataCartList(txtTable,txtNum,itemName, price, qty, subtotal))
            }
            notifyDataSetChanged()

            orderList.forEach{
                Log.d("Test", it.toString())
            }
            Log.e("test", orderList.size.toString())

        }

    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val stock: TextView = itemView.findViewById(R.id.tvStatus)
        val expandOption: ConstraintLayout = itemView.findViewById(R.id.expandMealMenu)
        val linear: LinearLayout = itemView.findViewById(R.id.linearTop)
        val txtQty: TextView = itemView.findViewById(R.id.txtQty)
        val btnAddQty: ImageButton = itemView.findViewById(R.id.btnAdd)
        val btnSubtract: ImageButton = itemView.findViewById(R.id.btnSub)
        val btnAddCart: Button = itemView.findViewById(R.id.btnAddToCart)


    }
}