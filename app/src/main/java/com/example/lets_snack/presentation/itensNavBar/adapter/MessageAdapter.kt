package com.example.lets_snack.presentation.itensNavBar.adapter

import android.graphics.Color
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.dto.MessageDto
import com.example.lets_snack.data.remote.dto.MessageDtoChat
import com.example.lets_snack.data.remote.dto.MessageUi

class MessageAdapter(private val messages: MutableList<MessageUi>, private val currentUser: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.message_text)
        val secondLayout: View = LayoutInflater.from(itemView.context).inflate(R.layout.fragment_chat_ia, null)
        val recyclerView: RecyclerView = secondLayout.findViewById(R.id.recycler_view)  // Acessando o RecyclerView no segundo layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        holder.messageTextView.layoutParams = layoutParams
        if (message.username == currentUser) {
            holder.messageTextView.visibility = View.VISIBLE
            holder.messageTextView.text = message.message
            layoutParams.gravity = Gravity.END
            holder.messageTextView.gravity = Gravity.START
            val marginLayoutParams = holder.messageTextView.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = 16
            holder.messageTextView.setBackgroundResource(R.drawable.message_send)
        }
        else {

            holder.messageTextView.visibility = View.VISIBLE
            holder.messageTextView.text = message.message

            layoutParams.gravity = Gravity.START
            val marginLayoutParams = holder.messageTextView.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = 16
            holder.messageTextView.setBackgroundResource(R.drawable.message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun updateMessages(newMessages: List<MessageUi>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    private fun animateTextTyping(recyclerView: RecyclerView, textView: TextView, message: String, delay: Long = 10) {
        textView.text = "" // Limpa o texto antes de iniciar
        val handler = android.os.Handler(Looper.getMainLooper())
        var index = 0
        val runnable = object : Runnable {
            override fun run() {
                if (index < message.length) {
                    Log.d("index", index.toString())
                    textView.visibility = View.VISIBLE
                    textView.text = textView.text.toString() + message[index]
                    index++

                    recyclerView.scrollToPosition(messages.size - 1)
                    handler.postDelayed(this, delay)
                }
            }
        }
        handler.post(runnable)
    }
}
