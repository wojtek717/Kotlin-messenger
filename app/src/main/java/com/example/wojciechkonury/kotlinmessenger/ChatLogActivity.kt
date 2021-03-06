package com.example.wojciechkonury.kotlinmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.wojciechkonury.kotlinmessenger.LatestMessagesActivity.Companion.currentUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>(USER_KEY)

        supportActionBar?.title = user.username

        recyclerview_chatlog.adapter = adapter

        listenForMessages(user)

        send_button_chat_log.setOnClickListener{
            performSendMessage(user)

            edittext_chat_log.text.clear()
            recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
        }
    }

    fun listenForMessages(user: User){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null){

                        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {

                            adapter.add(ChatToItem(chatMessage.text))
                        }

                        if (chatMessage.fromId == user.uid) {
                            adapter.add(ChatFromItem(chatMessage.text, user))
                            Log.d("Sprawdzonko", "WYKONANKO")
                        }
                        Log.d("Sprawdzonko", "toId: " + chatMessage.toId + "\n user.id: " + user.uid)
                }
                recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    fun performSendMessage(user: User){
//        val ref = FirebaseDatabase.getInstance().getReference("/message").push()

        val messageText = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid

        if (fromId == null){
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

        val chatMessage = ChatMessage(ref.key!!, messageText, fromId, toId, System.currentTimeMillis()/1000)

        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatLog", "Message send")
            }
            .addOnFailureListener{
                Log.d("ChatLog", "Cant send message")
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        toref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatLog", "Message send")
            }
            .addOnFailureListener{
                Log.d("ChatLog", "Cant send message")
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        latestMessageRef.setValue(chatMessage)
        latestMessageToRef.setValue(chatMessage)


    }
}

//left side
class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text

        val targetPlaceImage = viewHolder.itemView.imageView_from_row
        Picasso.get().load(user.profileImageUrl).into(targetPlaceImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}


class ChatToItem(val text: String) : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text

        val targetPlaceImage = viewHolder.itemView.imageView_to_row

        if(currentUser != null){
            Picasso.get().load(currentUser?.profileImageUrl).into(targetPlaceImage)
        }
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
