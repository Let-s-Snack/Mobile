package com.example.lets_snack.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import com.example.lets_snack.R
import com.example.lets_snack.databinding.ActivityRestrictAreaBinding

class RestrictArea : AppCompatActivity() {
    private lateinit var binding: ActivityRestrictAreaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestrictAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            loadUrl("http://ec2-52-20-248-152.compute-1.amazonaws.com:3000/")
        }
    }
}