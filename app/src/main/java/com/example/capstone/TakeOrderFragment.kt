package com.example.capstone


import android.app.ActionBar
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import java.text.DecimalFormat


class TakeOrderFragment(
    table: TextView,
    tableType: TextView,
    private var orderList: ArrayList<DataCartList>,
    private var currentActivity: Any?
) : Fragment() {
    private lateinit var dbref: FirebaseFirestore
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var mealMenuArrayList: ArrayList<DataMeal>
    private lateinit var beveragesMenuArrayList: ArrayList<DataBeverages>
    private lateinit var btnViewOrderList: ImageButton
    private lateinit var menuClicked: MenuClicked
    var currentTable: TextView = table
    var tableType: TextView = tableType
    val dec = DecimalFormat("#,###.00")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_take_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        menuClicked = MenuClicked()
        super.onViewCreated(view, savedInstanceState)
        val txttableType: TextView = view.findViewById(R.id.txtTableType)
        val txtTableNum: TextView = view.findViewById(R.id.txtTableNum)
        val btnMeal: Button = view.findViewById(R.id.btnMeal)
        val btnDrinks: Button = view.findViewById(R.id.btnDrinks)
        val btnMisc: Button = view.findViewById(R.id.btnCombo)
        val layoutManager = LinearLayoutManager(context)

        val btnBackOrder: ImageButton = view.findViewById(R.id.btnBackOrder)

        btnBackOrder.setOnClickListener {


            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SelectTableFragment())
                .commitNow()
        }
        btnViewOrderList = view.findViewById(R.id.btnViewOrderList)
        menuRecyclerView = view.findViewById(R.id.takeOrderMenuList)

        menuRecyclerView.layoutManager = layoutManager
        mealMenuArrayList = arrayListOf<DataMeal>()
        beveragesMenuArrayList = arrayListOf<DataBeverages>()

        txttableType.text = tableType.text
        txtTableNum.text = "${currentTable.text}"


        dbref = FirebaseFirestore.getInstance()


        getMealData(
            txttableType,
            txtTableNum,
            btnViewOrderList,
            currentActivity,
            menuClicked
        )

        if(orderList.size >0){
            btnViewOrderList.isEnabled = true
            btnViewOrderList.setBackgroundResource(R.drawable.floating_button_background)
        }else{
            btnViewOrderList.isEnabled = false
            btnViewOrderList.setBackgroundResource(R.drawable.floating_button_background_disabled)
        }

        val typeface: Typeface = Typeface.createFromAsset(requireActivity().assets, "carbon bl.ttf")

        val txtAvailableMenu: TextView = view.findViewById(R.id.txtAvailableMenu)
        txtAvailableMenu.typeface = typeface

        btnMeal.setBackgroundResource(R.drawable.category_selected_bg)
        btnMeal.setTextColor(Color.parseColor("#3498db"))

        btnMeal.setOnClickListener {
            btnMeal.setBackgroundResource(R.drawable.category_selected_bg)
            btnMeal.setTextColor(Color.parseColor("#3498db"))

            btnDrinks.setBackgroundColor(Color.parseColor("#3498db"))
            btnDrinks.setTextColor(Color.WHITE)

            btnMisc.setBackgroundColor(Color.parseColor("#3498db"))
            btnMisc.setTextColor(Color.WHITE)
            menuClicked.clickedBeverages(false)
            menuClicked.clickedMeal(true)
            getMealData(
                txttableType,
                txtTableNum,
                btnViewOrderList,
                currentActivity,
                menuClicked
            )

        }

        btnDrinks.setOnClickListener {
            menuClicked.clickedBeverages(true)
            menuClicked.clickedMeal(false)
            getBeverages(
                txttableType,
                txtTableNum,
                btnViewOrderList,
                currentActivity,
                menuClicked
            )

            btnDrinks.setBackgroundResource(R.drawable.category_selected_bg)
            btnDrinks.setTextColor(Color.parseColor("#3498db"))

            btnMeal.setBackgroundColor(Color.parseColor("#3498db"))
            btnMeal.setTextColor(Color.WHITE)

            btnMisc.setBackgroundColor(Color.parseColor("#3498db"))
            btnMisc.setTextColor(Color.WHITE)

        }





        if (orderList.size == 0) {
            btnViewOrderList.isEnabled = false
            btnViewOrderList.setBackgroundResource(R.drawable.floating_button_background_disabled)
        }

        btnViewOrderList.setOnClickListener {
            if (btnViewOrderList.isEnabled) {
                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .add(
                        R.id.fragment_container, CartFragment(
                            orderList,
                            tableType,
                            currentTable,
                            currentActivity
                        )
                    ).commitNow()
            }
        }

        btnMisc.setOnClickListener {

            btnMisc.setTextColor(Color.parseColor("#3498db"))

            btnMeal.setBackgroundColor(Color.parseColor("#3498db"))
            btnMeal.setTextColor(Color.WHITE)

            btnDrinks.setBackgroundColor(Color.parseColor("#3498db"))
            btnDrinks.setTextColor(Color.WHITE)

            val dialogBinding = layoutInflater.inflate(R.layout.misc_custom_dialogbox, null)

            val myDialog = Dialog(requireContext())

            val etName: EditText = dialogBinding.findViewById(R.id.etName)
            val etPrice: EditText = dialogBinding.findViewById(R.id.etPrice)
            val etQty: EditText = dialogBinding.findViewById(R.id.etQuantity)
            val btnAddCart:Button = dialogBinding.findViewById(R.id.btnAddCart)
            btnMisc.setBackgroundResource(R.drawable.category_selected_bg)
            myDialog.setContentView(dialogBinding)
            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()
            val window: Window? = myDialog.window
            window?.setLayout(1000, ActionBar.LayoutParams.WRAP_CONTENT);
            btnAddCart.setOnClickListener{
                if(etName.text.toString() == "" ||
                    etPrice.text.toString() == "" ||
                    etQty.text.toString() == ""){
                    Toast.makeText(requireContext(), "Empty fields detected.", Toast.LENGTH_SHORT).show()
                }
                else{
                    val estName = etName.text.toString()
                    val qtys  = Integer.parseInt(etQty.text.toString())
                    val dialogClickListener =
                        DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    if (checkExistingItem(estName, orderList)) {
                                        for (o in orderList) {
                                            if (o.ItemName == estName) {
                                                o.editQty(qtys)
                                            }
                                        }
                                    }else{
                                        btnViewOrderList.isEnabled = true
                                        btnViewOrderList.setBackgroundResource(R.drawable.floating_button_background)
                                        val tsLong = System.currentTimeMillis() / 1000
                                        val ts = tsLong.toString()
                                        val price = etPrice.text.toString().toLong()
                                        val subtotal = price * qtys
                                        val itemCode: String=estName[0].toString() + ts
                                        orderList.add(
                                            DataCartList(
                                                itemCode,
                                                Integer.parseInt(txtTableNum.text.toString()),
                                                estName, price, qtys, subtotal, "misc",
                                                status = false,
                                                isBucket = false,
                                                ImageUrl = ""
                                            )
                                        )
                                        myDialog.cancel()
                                    }
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {}
                            }
                        }

                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Are you sure you want to add this item?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show()
                }
            }


            myDialog.setOnCancelListener{


                if(menuClicked.isMeal == true && menuClicked.isBeverages == false){

                    getMealData(
                        txttableType,
                        txtTableNum,
                        btnViewOrderList,
                        currentActivity,
                        menuClicked
                    )
                    btnMeal.setBackgroundResource(R.drawable.category_selected_bg)
                    btnMeal.setTextColor(Color.parseColor("#3498db"))

                    btnDrinks.setBackgroundColor(Color.parseColor("#3498db"))
                    btnDrinks.setTextColor(Color.WHITE)
                }else{
                    getBeverages(
                        txttableType,
                        txtTableNum,
                        btnViewOrderList,
                        currentActivity,
                        menuClicked
                    )

                    btnDrinks.setBackgroundResource(R.drawable.category_selected_bg)
                    btnDrinks.setTextColor(Color.parseColor("#3498db"))
                    btnMeal.setBackgroundColor(Color.parseColor("#3498db"))
                    btnMeal.setTextColor(Color.WHITE)
                }


                btnMisc.setBackgroundColor(Color.parseColor("#3498db"))
                btnMisc.setTextColor(Color.WHITE)
            }
        }

    }
    private fun checkExistingItem(estName: String, orderList: ArrayList<DataCartList>): Boolean {
        for (i in orderList) {
            if (i.ItemName == estName) {
                return true
            }
        }
        return false

    }
    private fun getBeverages(
        txttableType: TextView,
        txtTableNum: TextView,
        btnViewOrderList: ImageButton,
        currentActivity: Any?,
        menuClicked: MenuClicked,
        ) {
        beveragesMenuArrayList.clear()

        dbref.collection("beverages").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if (p1 != null) {
                    Log.e("Firestore Error", p1.message.toString())
                    return
                }
                for (dc: DocumentChange in p0?.documentChanges!!) {

                    val data = dc.document.toObject(DataBeverages::class.java)
                    var obj: Any? = null
                    var index: Int? = null
                    if (dc.type == DocumentChange.Type.ADDED) {
                        if(menuClicked.isMeal == false){
                            beveragesMenuArrayList.add(data)
                            menuRecyclerView.adapter = MenuListBeveragesAdapter(
                                beveragesMenuArrayList,
                                txttableType,
                                txtTableNum,
                                orderList,
                                btnViewOrderList,
                                currentActivity

                            )
                        }

                    }
                    if (dc.type == DocumentChange.Type.MODIFIED) {
                        if(menuClicked.isMeal == false){
                            for (i in beveragesMenuArrayList) {
                                if (i.BeverageName == data.BeverageName) {
                                    obj = i
                                    index = beveragesMenuArrayList.indexOf(i)
                                }
                            }
                            if (obj != null) {

                                beveragesMenuArrayList[index as Int] = data
                            }
                            menuRecyclerView.adapter = MenuListBeveragesAdapter(
                                beveragesMenuArrayList,
                                txttableType,
                                txtTableNum,
                                orderList,
                                btnViewOrderList,
                                this@TakeOrderFragment.currentActivity

                            )
                        }
                    }
                    if(dc.type == DocumentChange.Type.REMOVED){
                       for(i in beveragesMenuArrayList){
                           if(i.BeverageName == data.BeverageName){
                               beveragesMenuArrayList.remove(i)
                           }
                       }
                        menuRecyclerView.adapter = MenuListBeveragesAdapter(
                            beveragesMenuArrayList,
                            txttableType,
                            txtTableNum,
                            orderList,
                            btnViewOrderList,
                            this@TakeOrderFragment.currentActivity

                        )
                    }


                }

            }

        })

    }

    private fun getMealData(
        txttableType: TextView,
        txtTableNum: TextView,
        btnViewOrderList: ImageButton,
        currentActivity: Any?,
        menuClicked: MenuClicked,
    ) {
        mealMenuArrayList.clear()
        val meal = dbref.collection("meals")
       meal.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if (p1 != null) {
                    Log.e("Firestore Error", p1.message.toString())
                    return
                }
                for (dc: DocumentChange in p0?.documentChanges!!) {
                    val data = dc.document.toObject(DataMeal::class.java)
                    var obj: Any? = null
                    var index: Int? = null
                    if (dc.type == DocumentChange.Type.ADDED) {
                       if(menuClicked.isBeverages == null || menuClicked.isBeverages == false){
                           mealMenuArrayList.add(data)
                           menuRecyclerView.adapter = MenuListMealAdapter(
                               mealMenuArrayList, txttableType, txtTableNum, orderList,
                               btnViewOrderList, currentActivity
                           )
                       }

                    }
                    if (dc.type == DocumentChange.Type.MODIFIED) {
                      if(menuClicked.isBeverages == null || menuClicked.isBeverages == false){
                          for (i in mealMenuArrayList) {
                              if (i.MealName == data.MealName) {
                                  obj = i
                                  index = mealMenuArrayList.indexOf(i)
                              }
                          }
                          if (obj != null) {

                              mealMenuArrayList[index as Int] = data
                          }
                          menuRecyclerView.adapter = MenuListMealAdapter(
                              mealMenuArrayList, txttableType, txtTableNum, orderList,
                              btnViewOrderList, currentActivity
                          )
                      }
                    }

                    if(dc.type == DocumentChange.Type.REMOVED){
                        for(i in mealMenuArrayList){
                            if(i.MealName == data.MealName){
                                mealMenuArrayList.remove(i)
                            }
                        }
                        menuRecyclerView.adapter = MenuListMealAdapter(
                            mealMenuArrayList, txttableType, txtTableNum, orderList,
                            btnViewOrderList, currentActivity
                        )
                    }


                }


            }

        })


    }


}