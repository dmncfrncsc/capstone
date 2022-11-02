package com.example.capstone


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class FirstTimeLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_login)

        val etPasswords: EditText = findViewById(R.id.etPasswords)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val etConfirmPassword: EditText = findViewById(R.id.etConfirmPassword)

        val btnConfirmPassword: Button = findViewById(R.id.btnChangePassword)
        btnConfirmPassword.setBackgroundResource(R.drawable.btn_disabled_bg)
        btnConfirmPassword.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            if (etPasswords.text.toString()
                                    .equals(etConfirmPassword.text.toString(), ignoreCase = true)
                            ) {
                                val mAuth = Firebase.auth
                                val user = mAuth.currentUser

                                user!!.updatePassword(etPasswords.text.toString()).addOnCompleteListener {
                                        task ->
                                        if(task.isSuccessful){

                                            val dbRef = FirebaseFirestore.getInstance()

                                            dbRef.collection("employees")
                                                .whereEqualTo("Email", user.email.toString()).get()
                                                .addOnSuccessListener { result ->
                                                    for (docs in result) {
                                                        docs.reference.update("IsFirstLogin", false)
                                                    }
                                                }

                                           Toast.makeText(
                                                this,
                                                "Successfully changed password.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            mAuth.signOut()
                                            val i = Intent(
                                                applicationContext,
                                                SignInActivity::class.java
                                            )
                                            startActivity(i)

                                        }else{
                                            Toast.makeText(
                                                this,
                                                task.exception.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Password mismatch.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Confirm change password?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }
    }

    override fun onBackPressed() {

    }
}