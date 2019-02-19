package com.example.wojciechkonury.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var selectedPhotoUri: Uri? = null

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
            selectedPhotoUri = data.data //where the image is stored
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver ,selectedPhotoUri) //bitmap of selected photo

            selectedphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f
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
                Log.d("FirebaseRegistration", "Successfully created user with uid: " + it.result.user.uid)

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d("FirebaseRegistration", "Failed to create user: " + it.message)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null){
            Log.d("FirebaseRegistration", "Photo uri null")

            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("FirebaseRegistration", "Successfully uploaded image: " + it.metadata?.path)

                ref.downloadUrl.addOnSuccessListener {
                    saveUserToDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("FirebaseRegistration", "Failed to upload photo")
            }
    }

    private fun saveUserToDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("FirebaseRegistration", "Succesfully saved user to database")
            }
            .addOnFailureListener {
                Log.d("FirebaseRegistration", "Failed to database: " + it.message)
            }
    }
}

class User(val uid: String, val username: String, val profileImageUrl: String)
