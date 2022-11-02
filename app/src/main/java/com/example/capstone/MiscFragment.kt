package com.example.capstone

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MiscFragment(
    private val orderList: ArrayList<DataCartList>,
    private val txttableType: TextView,
    private val txtTableNum: TextView,
    private val btnViewOrderList: ImageButton,
    private val currentActivity: Any?
) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_misc,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnMisc: Button = view.findViewById(R.id.btnMisc)
        val etName: EditText = view.findViewById(R.id.etName)
        val etPrice: EditText = view.findViewById(R.id.etPrice)
        val etQty: EditText = view.findViewById(R.id.etQuantity)
        val btnAddCart:Button = view.findViewById(R.id.btnAddCart)
        btnMisc.setBackgroundResource(R.drawable.category_selected_bg)
        btnMisc.setTextColor(Color.parseColor("#3498db"))
        btnViewOrderList.setOnClickListener {
            if (btnViewOrderList.isEnabled) {
                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container, CartFragment(
                            orderList,
                            txttableType,
                            txtTableNum,
                            currentActivity
                        )
                    ).commitNow()
            }
        }
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
                                }
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {}
                        }
                    }

                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
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
}