package com.example.wojciechkonury.kotlinmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener{
            performSignIn()
        }

        backtoregistration_textview_login.setOnClickListener {
            finish()
        }
    }

    private fun performSignIn(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Email or password is empty", Toast.LENGTH_SHORT).show()

            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if is successful
                Log.d("Firebase", "Successfully signed in with uid:" + it.result.user.uid)
            }
            .addOnFailureListener{
                Log.d("Firebase", "Failed to sign in: " + it.message)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}
