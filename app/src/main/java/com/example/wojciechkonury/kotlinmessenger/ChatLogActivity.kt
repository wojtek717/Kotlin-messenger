package com.example.wojciechkonury.kotlinmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatToItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatFromItem())


        recyclerview_chatlog.adapter = adapter
    }
}


class ChatFromItem : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}


class ChatToItem : Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
