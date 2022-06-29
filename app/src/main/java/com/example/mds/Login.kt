package com.example.mds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class Login : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editEmail = findViewById(R.id.edit_email)
        editPassword = findViewById(R.id.edit_password)
        btnLogin = findViewById(R.id.btnLogin)
        mAuth = FirebaseAuth.getInstance()


        val currentuser = mAuth.currentUser
        if(currentuser != null) {
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
        }
        else {
            login()
        }

    }
    private fun login() {
        btnLogin.setOnClickListener()
        {

            if (TextUtils.isEmpty((editEmail.text.toString()))) {
                editEmail.setError("Please enter email")
                return@setOnClickListener
            } else if (TextUtils.isEmpty(editPassword.text.toString())) {
                editPassword.setError("Please enter password")
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(editEmail.text.toString(), editPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = mAuth.currentUser

                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    return@OnCompleteListener
                                }

                                val  token = task.result.toString()
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(user?.uid.toString()).child("deviceToken")
                                    .setValue(token).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        Log.w("User ", user?.uid.toString())
                                    } else {
                                        Log.w("User ", "Failed ")
                                    }
                                }
                            })
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                        finish()
                    }
                    else {
                       Toast.makeText(
                            this@Login,
                            "Login failed, please try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
        val btnRegister = findViewById<TextView>(R.id.registerText) as TextView
        btnRegister.setOnClickListener{
            startActivity(Intent(this@Login, SignUp::class.java))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
            finish()
        }

    }


}