package com.example.wojciechkonury.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            performRegistration()
        }

        already_have_account_text_view_register.setOnClickListener {
            Log.d("RegisterActivity", "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            val uri = data.data //where the image is stored
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver ,uri) //bitmap of selected photo
            val bitmapDrawable = BitmapDrawable(bitmap) //drawable bitmap of selected photo

            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }


    private fun performRegistration(){
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()


        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Email or password is empty", Toast.LENGTH_SHORT).show()

            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if Successful
                Log.d("Firebase", "Successfully created user with uid: " + it.result.user.uid)
            }
            .addOnFailureListener{
                Log.d("Firebase", "Failed to create user: " + it.message)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}
