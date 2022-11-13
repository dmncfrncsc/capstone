package com.example.capstone

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val tv_app_name = findViewById<TextView>(R.id.tv_app_name)
        val btnSignIn = findViewById<Button>(R.id.btnSignin)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        var etUsername: EditText = findViewById(R.id.etUsername)
        var etPassword: EditText = findViewById(R.id.etPassword)
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        btnSignIn.typeface = typeface
        tv_app_name.typeface = typeface
        btnSignIn.setOnClickListener {
            loginUser(etUsername, etPassword)
        }
        val user = Firebase.auth.currentUser
        user?.let {
            val name = user.email
        }
        if (user != null) {
            val users = arrayListOf<DataUser>()
            val dbRef = FirebaseFirestore.getInstance().collection("employees")
            dbRef.addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    users.clear()
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        var obj: Any? = null
                        var index: Int? = null
                        val data = dc.document.toObject(DataUser::class.java)
                        if (dc.type == DocumentChange.Type.ADDED) {
                            users.add(data)
                        }
                        if (dc.type == DocumentChange.Type.MODIFIED) {
                            for (i in users) {
                                if (i.Email == data.Email) {
                                    obj = i
                                    index = users.indexOf(i)
                                }
                            }
                            if (obj != null) {
                                users[index as Int] = data
                            }
                        }
                          if(dc.type == DocumentChange.Type.REMOVED){
                              for(i in users){
                                  if(i.Email == data.Email){
                                      users.remove(i)
                                  }
                              }
                          }
                    }
                    for (i in users) {
                        if (i.Email == user.email.toString()) {
                            val i = Intent(
                                applicationContext,
                                MainActivity::class.java
                            )
                            startActivity(i)
                        }
                    }
                }
            })
        }
    }

    private fun loginUser(etUsername: EditText, etPassword: EditText) {
        val mAuth = Firebase.auth
        val dbRef = FirebaseFirestore.getInstance().collection("employees")
        val users = arrayListOf<DataUser>()
        val email = etUsername.text.toString()
        val password = etPassword.text.toString()
        if (TextUtils.isEmpty(email)) {
            etUsername.error = "Email cannot be empty"

        } else if (TextUtils.isEmpty(password)) {
            etPassword.error = "Password cannot be empty"
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dbRef.addSnapshotListener(object : EventListener<QuerySnapshot> {
                            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                                users.clear()
                                if (error != null) {
                                    Log.e("Firestore Error", error.message.toString())
                                    return
                                }
                                for (dc: DocumentChange in value?.documentChanges!!) {
                                    var obj: Any? = null
                                    var index: Int? = null
                                    val data = dc.document.toObject(DataUser::class.java)
                                    if (dc.type == DocumentChange.Type.ADDED) {
                                        users.add(data)
                                    }
                                    if (dc.type == DocumentChange.Type.MODIFIED) {
                                        for (i in users) {
                                            if (i.Email == data.Email) {
                                                obj = i
                                                index = users.indexOf(i)
                                            }
                                        }
                                        if (obj != null) {
                                            users[index as Int] = data
                                        }
                                    }
                                    if (dc.type == DocumentChange.Type.REMOVED) {
                                        for (i in users) {
                                            if (i.Email == data.Email) {
                                                users.remove(i)
                                            }
                                        }
                                    }
                                }
                                for (o in users) {
                                    if (email.equals(o.Email, ignoreCase = true)) {
                                        if (o.Position.equals("waiter", ignoreCase = true)) {
                                            if (o.Status == false) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "This account is disabled.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                if (o.IsFirstLogin == true) {
                                                    val i = Intent(
                                                        applicationContext,
                                                        FirstTimeLogin::class.java
                                                    )
                                                    startActivity(i)
                                                } else {
                                                    val i = Intent(
                                                        applicationContext,
                                                        MainActivity::class.java
                                                    )
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Logged in successfully.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    startActivity(i)
                                                }
                                            }
                                        }
                                        else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Incorrect email or password.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        })
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Incorrect email or password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    override fun onBackPressed() {
    }
}