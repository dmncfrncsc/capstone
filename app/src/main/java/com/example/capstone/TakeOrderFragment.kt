package com.example.capstone

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import java.text.DecimalFormat
import java.text.NumberFormat


class TakeOrderFragment(table: TextView) : Fragment() {
    private lateinit var dbref: DatabaseReference
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuArrayList: ArrayList<menu_meal>
    private lateinit var orderList: ArrayList<DataCartList>
    private lateinit var cartRecyclerView: RecyclerView
    var currentTable: TextView = table
    var tPrice: Long = 0
    val dec = DecimalFormat("#,###.00")

    val numFormat: NumberFormat = NumberFormat.getCurrencyInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_take_order, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        menuRecyclerView = view.findViewById(R.id.takeOrderMenuList)
        menuRecyclerView.layoutManager = layoutManager
        menuArrayList = arrayListOf<menu_meal>()



        orderList = arrayListOf<DataCartList>()



        var btnViewCart: Button = requireView().findViewById(R.id.viewOrderList)

        btnViewCart.setOnClickListener{
            if (orderList.size.equals(0)){
                Toast.makeText(context,"No orders yet", Toast.LENGTH_SHORT).show()
            }else{

                openCartDialog()
            }
        }
        getData()


    }



    private fun openCartDialog(){

        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.cart_dialog)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT

        val layoutManager = LinearLayoutManager(context)
        cartRecyclerView = dialog.findViewById(R.id.orderItemList)
        cartRecyclerView.layoutManager = layoutManager
        cartRecyclerView.adapter = CartAdapter(orderList)


        dialog.show()
        dialog.window!!.attributes = lp

       var btnConfirmOrder: Button = dialog.findViewById(R.id.btnConfirmOrder)

        btnConfirmOrder.setOnClickListener{
            Toast.makeText(context, "ORDER SUCCESS", Toast.LENGTH_SHORT).show()

            var table:String = currentTable.text.toString()
            dbref= FirebaseDatabase.getInstance().getReference("Orders").child(table)

            dbref.setValue(orderList)
            dialog.dismiss()
            orderList.clear()

        }

    }
    private fun getData() {
        dbref = FirebaseDatabase.getInstance().getReference("meals")

        dbref.addValueEventListener(object: ValueEventListener, MenuListAdapter.OnItemClickListner{
            override fun onDataChange(p0: DataSnapshot) {


                if(p0.exists()){
                    menuArrayList.clear();
                    for(mealSnapshot in p0.children){
                        val meal = mealSnapshot.getValue(menu_meal::class.java)
                        menuArrayList.add(meal!!)
                    }

                    menuRecyclerView.adapter = MenuListAdapter(menuArrayList, this)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(position: Int) {


                var currentItem = menuArrayList[position]

                var name = currentItem.MealName
                var price = currentItem.Price
                var status = currentItem.Status

                if(status != false){
                    showTakeOrderDialog(name!!, price!!);
                }


            }
        })
    }

    private fun showTakeOrderDialog(food_name: String, food_price: Long) {

        var newQty: Int
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.take_order_dialog)


        var foodName:TextView = dialog.findViewById(R.id.foodName)
        var foodPrice:TextView = dialog.findViewById(R.id.foodPrice)
        var quantity: TextView = dialog.findViewById(R.id.tvQuantity)
        var totalPrice: TextView = dialog.findViewById(R.id.tvTotalPrice)
        var btnAdd: ImageButton = dialog.findViewById(R.id.btnAdd)
        var btnSubtract: ImageButton = dialog.findViewById(R.id.btnSub)
        var btnCancel: Button = dialog.findViewById(R.id.btnCancel)
        var btnConfirm: Button = dialog.findViewById(R.id.btnTakeOrder)
        foodName.text = food_name


        if(quantity.text.toString()=="#"){
            quantity.text = (1).toString()
            newQty= Integer.parseInt(quantity.text.toString())
            foodPrice.text = dec.format(food_price).toString()
        }

        getTotalPrice(food_price, quantity.text as String, totalPrice)


        btnAdd.setOnClickListener{

            newQty = Integer.parseInt(quantity.text.toString()) + 1
            quantity.text = newQty.toString()
            getTotalPrice(food_price,newQty.toString(), totalPrice)
        }

        btnSubtract.setOnClickListener{
            if(quantity.text.toString() != "1"){
                newQty = Integer.parseInt(quantity.text.toString()) - 1
                quantity.text = newQty.toString()
                getTotalPrice(food_price,newQty.toString(), totalPrice)
            }
        }


        btnCancel.setOnClickListener{
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener{

            var name = foodName.text.toString()
            var price = (food_price)
            var qty = Integer.parseInt(quantity.text.toString())

           orderList.add(DataCartList(Integer.parseInt(currentTable.text.toString()),name,price,qty,(price*qty)))

            dialog.dismiss()

              for(element in orderList){
               Log.d("TEST", "${element}")
           }
        }
        dialog.show()
    }

    private fun getTotalPrice(food_price: Long, quantity:String, totalPrice:TextView) {

        var total: Long = food_price * Integer.parseInt(quantity)


        totalPrice.text = dec.format(total).toString()

    }

}