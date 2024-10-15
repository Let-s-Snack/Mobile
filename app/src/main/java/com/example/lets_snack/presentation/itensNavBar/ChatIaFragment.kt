package com.example.lets_snack.presentation.itensNavBar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lets_snack.databinding.FragmentChatIaBinding
import com.example.lets_snack.presentation.itensNavBar.adapter.MessageAdapter
import org.json.JSONArray
import org.json.JSONObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException

class ChatIaFragment : Fragment() {

    private var _binding: FragmentChatIaBinding? = null
    private val binding get() = _binding!!

    private var eventos = "Você é um assistente"

    private var messageAdapter: MessageAdapter?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatIaBinding.inflate(inflater, container, false)
        binding.messageInput.setEndIconOnClickListener {
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun enviar(){
        val url = "https://api.openai.com/v1/chat/completions"
        val apiKey = "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"
        val prompt = binding.messageInputText.text.toString()
        Log.d("Prompt",prompt)

        val json = JSONObject()
        try {
            json.put("model", "gpt-3.5-turbo")

            val system = JSONObject()
            system.put("role", "system")
            system.put("content",eventos)

            val user = JSONObject()
            user.put("role", "user")
            user.put("content",prompt)

            val array = JSONArray()
            array.put(system)
            array.put(user)

            json.put("messages",array)
        }catch (e:Exception){
            Log.d("Error",e.toString())
        }
        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        val curl = OkHttpClient()
        curl.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonObject = JSONObject(response.body!!.string())
                val content = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                Log.d("content",content)
                Handler(Looper.getMainLooper()).post {
//                    binding.responseGpt.text = content
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("api-gpt",e.message.toString())
            }
        })
    }
}
