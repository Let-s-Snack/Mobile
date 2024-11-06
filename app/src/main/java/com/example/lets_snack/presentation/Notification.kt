package com.example.lets_snack.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Deu green", Toast.LENGTH_SHORT).show()
    }
}
