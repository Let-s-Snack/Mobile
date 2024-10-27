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

class MessageAdapter(private val messages: MutableList<MessageDto>, private val currentUser: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val usernameColorMap = HashMap<String, Int>()
    private var isLast = false

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
            // Define o texto formatado no TextView
            holder.messageTextView.text = message.message
//            holder.recyclerView.scrollToPosition(messages.size - 1)
            layoutParams.gravity = Gravity.END
            holder.messageTextView.gravity = Gravity.START
            val marginLayoutParams = holder.messageTextView.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = 16
            holder.messageTextView.setBackgroundResource(R.drawable.message_send)
        }
        else {

            val username = message.username
            val usernameColor = usernameColorMap.getOrPut(username) {getColorFromString(username) }

            val spannableString = SpannableString(message.username + "\n" + message.message)
            spannableString.setSpan(
                ForegroundColorSpan(usernameColor),
                0,
                username.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.messageTextView.text = message.message

            Log.d("isLastMessage", "${isLast}")
            if (isLast) {
                if (position == messages.size - 1) {
                    animateTextTyping(holder.recyclerView, holder.messageTextView, message.message)
                    isLast = false
                }
                else {
                    holder.messageTextView.text = message.message
                }
            }
            else{
                holder.messageTextView.text = message.message
//                holder.recyclerView.scrollToPosition(messages.size - 1)
            }

            layoutParams.gravity = Gravity.START
            val marginLayoutParams = holder.messageTextView.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = 16
            holder.messageTextView.setBackgroundResource(R.drawable.message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: MessageDto) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun updateMessages(newMessages: List<MessageDto>, isLast: Boolean) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
        this.isLast = isLast
    }

    private fun getColorFromString(text: String): Int {
        return Color.rgb(243, 140, 52)
    }

    private fun animateTextTyping(recyclerView: RecyclerView, textView: TextView, message: String, delay: Long = 50) {
        textView.text = "" // Limpa o texto antes de iniciar
        val handler = android.os.Handler(Looper.getMainLooper())
        var index = 0

        val runnable = object : Runnable {
            override fun run() {
                if (index < message.length) {
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
