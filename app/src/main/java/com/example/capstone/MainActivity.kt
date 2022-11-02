package com.example.capstone

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.util.*


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnOrder = findViewById<Button>(R.id.btnStartOrder)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val calendar: Date = Calendar.getInstance().time
        val currentDate: String = DateFormat.getDateInstance(DateFormat.FULL).format(calendar)
        //var txtDate: TextView = findViewById(R.id.txtDate)
        //txtDate.text = currentDate
        btnOrder.setOnClickListener{
            val selTableFrag: Fragment = SelectTableFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, selTableFrag);
            transaction.commit()
        }
        btnLogout.setOnClickListener{

            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            var  auth = Firebase.auth
                            auth.signOut()
                            val i = Intent(
                                applicationContext,
                                SignInActivity::class.java
                            )

                            startActivity(i)
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }
    }

    override fun onBackPressed() {

    }

}