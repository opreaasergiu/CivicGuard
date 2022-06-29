package com.example.mds


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging


class SignUp : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    var databaseReference :  DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userName = findViewById(R.id.user_name)
        editEmail = findViewById(R.id.edit_email)
        editPassword = findViewById(R.id.edit_password)
        btnSignUp = findViewById(R.id.btnSignUp)


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")



        btnSignUp.setOnClickListener()
        {
            if (TextUtils.isEmpty(userName.text.toString())) {
                userName.setError("Please enter username")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty((editEmail.text.toString()))) {
                editEmail.setError("Please enter email")
                return@setOnClickListener
            } else if (TextUtils.isEmpty(editPassword.text.toString())) {
                editPassword.setError("Please enter password")
                return@setOnClickListener
            }
            //signup()
            signup(userName.text.toString(),editEmail.text.toString(), editPassword.text.toString())
        }

        val btnLogin = findViewById<TextView>(R.id.loginText) as TextView
        btnLogin.setOnClickListener{
            startActivity(Intent(this@SignUp, Login::class.java))
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
            finish()
        }

    }
    private fun signup(username: String, email:String, password: String)
    {
        if (SignUpValidation.isValid(username, email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        var token:String? = null
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    return@OnCompleteListener
                                }

                                token = task.result.toString()

                                val user: User = User(
                                    username,
                                    email,
                                    "user",
                                    token
                                )

                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(user).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val intent = Intent(this@SignUp, MainActivity::class.java)
                                            startActivity(intent)
                                            overridePendingTransition(R.anim.slide_in_right,
                                                R.anim.slide_out_left);
                                            finish()

                                            Toast.makeText(this@SignUp, "Registration Success. ", Toast.LENGTH_LONG).show()
                                            finish()
                                        } else {
                                            Toast.makeText(this@SignUp, "Registration failed, please try again with different data", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                            })


                    } else {

                        Toast.makeText(this@SignUp, "Registration failed, please try again", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else {
            Toast.makeText(this@SignUp, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }
//    private  fun signup( )
//    {
//        var username: String = userName.text.toString()
//        var email:String = editEmail.text.toString()
//        var password: String = editPassword.text.toString()
//            mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        var token:String? = null
//                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
//                            OnCompleteListener { task ->
//                            if (!task.isSuccessful) {
//                                return@OnCompleteListener
//                            }
//
//                                token = task.result.toString()
//
//                            val user: User = User(
//                                username,
//                                email,
//                                "user",
//                                token
//                                )
//
//                            FirebaseDatabase.getInstance().getReference("Users")
//                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
//                                .setValue(user).addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        val intent = Intent(this@SignUp, MainActivity::class.java)
//                                        startActivity(intent)
//                                        overridePendingTransition(R.anim.slide_in_right,
//                                            R.anim.slide_out_left);
//                                        finish()
//
//                                        Toast.makeText(this@SignUp, "Registration Success. ", Toast.LENGTH_LONG).show()
//                                        finish()
//                                    } else {
//                                        Toast.makeText(this@SignUp, "Registration failed, please try again with different data", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//
//                                })
//
//
//                    } else {
//
//                        Toast.makeText(this@SignUp, "Registration failed, please try again", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }


}