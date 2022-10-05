package com.example.capstone

import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import java.text.DecimalFormat


class TakeOrderFragment(table: TextView, tableType: TextView) : Fragment() {
    private lateinit var dbref: FirebaseFirestore
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuArrayList: ArrayList<menu_meal>
    private lateinit var orderList: ArrayList<DataCartList>
    private lateinit var cartRecyclerView: RecyclerView
    var currentTable: TextView = table
    var tableType: TextView = tableType
    val dec = DecimalFormat("#,###.00")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_take_order, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val txttableType: TextView = view.findViewById(R.id.txtTableType)
        val txtTableNum: TextView = view.findViewById(R.id.txtTableNum)
        val btnMeal: Button = view.findViewById(R.id.btnMeal)
        val btnDrinks: Button = view.findViewById(R.id.btnDrinks)
        val btnCombo: Button = view.findViewById(R.id.btnCombo)
        val layoutManager = LinearLayoutManager(context)



        menuRecyclerView = view.findViewById(R.id.takeOrderMenuList)

        menuRecyclerView.layoutManager = layoutManager
        menuArrayList = arrayListOf<menu_meal>()

        txttableType.text= tableType.text
        txtTableNum.text ="${currentTable.text}"



        orderList = arrayListOf<DataCartList>()


/*
        var btnViewCart: ImageButton = requireView().findViewById(R.id.viewOrderList)

        btnViewCart.setOnClickListener{
            if (orderList.size.equals(0)){
                Toast.makeText(context,"No orders yet", Toast.LENGTH_SHORT).show()
            }else{

                openCartDialog()
            }
        }*/


        val typeface: Typeface = Typeface.createFromAsset(requireActivity().assets, "carbon bl.ttf")

        val txtAvailableMenu: TextView = view.findViewById(R.id.txtAvailableMenu)
        txtAvailableMenu.typeface = typeface

        btnMeal.setBackgroundResource(R.drawable.category_selected_bg)
        btnMeal.setTextColor(Color.parseColor("#3498db"))
        
        btnMeal.setOnClickListener{

            btnMeal.setBackgroundResource(R.drawable.category_selected_bg)
            btnMeal.setTextColor(Color.parseColor("#3498db"))

            btnDrinks.setBackgroundColor(Color.parseColor("#3498db"))
            btnDrinks.setTextColor(Color.WHITE)

            btnCombo.setBackgroundColor(Color.parseColor("#3498db"))
            btnCombo.setTextColor(Color.WHITE)
        }

        btnDrinks.setOnClickListener{

            btnDrinks.setBackgroundResource(R.drawable.category_selected_bg)
            btnDrinks.setTextColor(Color.parseColor("#3498db"))

            btnMeal.setBackgroundColor(Color.parseColor("#3498db"))
            btnMeal.setTextColor(Color.WHITE)

            btnCombo.setBackgroundColor(Color.parseColor("#3498db"))
            btnCombo.setTextColor(Color.WHITE)
        }

        btnCombo.setOnClickListener{

            btnCombo.setBackgroundResource(R.drawable.category_selected_bg)
            btnCombo.setTextColor(Color.parseColor("#3498db"))

            btnMeal.setBackgroundColor(Color.parseColor("#3498db"))
            btnMeal.setTextColor(Color.WHITE)

            btnDrinks.setBackgroundColor(Color.parseColor("#3498db"))
            btnDrinks.setTextColor(Color.WHITE)
        }




        menuRecyclerView.adapter = MenuListAdapter(menuArrayList, txttableType, txtTableNum, orderList)
        getData()


    }

    private fun getData() {
        dbref = FirebaseFirestore.getInstance()
        dbref.collection("meals").addSnapshotListener(object: EventListener<QuerySnapshot>{
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if(p1 != null){
                    Log.e("Firestore Error", p1.message.toString())
                    return
                }
                for(dc: DocumentChange in p0?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){

                        menuArrayList.add(dc.document.toObject(menu_meal::class.java))
                    }
                }

                menuRecyclerView.adapter!!.notifyDataSetChanged()


            }

        })
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
            /*dbref= FirebaseDatabase.getInstance().getReference("Orders").child(table)

            dbref.setValue(orderList)
            dialog.dismiss()
            orderList.clear()
*/
        }

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

         // orderList.add(DataCartList(Integer.parseInt(currentTable.text.toString()),name,price,qty,(price*qty)))

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