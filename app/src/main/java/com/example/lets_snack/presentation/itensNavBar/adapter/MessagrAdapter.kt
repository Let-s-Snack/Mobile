package com.example.lets_snack.presentation.itensNavBar.adapter


import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.message_text)
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
            layoutParams.gravity = Gravity.END
            holder.messageTextView.gravity = Gravity.START
            val marginLayoutParams = holder.messageTextView.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.marginEnd = 13
            marginLayoutParams.topMargin = 13
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

            holder.messageTextView.text = spannableString

            layoutParams.gravity = Gravity.START
            val marginLayoutParams = holder.messageTextView.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.marginStart = 13
            marginLayoutParams.topMargin = 13
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

    private fun getColorFromString(text: String): Int {
        return Color.rgb(
            text.hashCode() and 0xFF,
            (text.hashCode() shr 8) and 0xFF,
            (text.hashCode() shr 16) and 0xFF
        )
    }
}
