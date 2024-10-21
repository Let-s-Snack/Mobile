package com.example.lets_snack.presentation.itensNavBar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lets_snack.data.remote.dto.MessageDto
import com.example.lets_snack.databinding.FragmentChatIaBinding
import com.example.lets_snack.presentation.itensNavBar.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject

class ChatIaFragment : Fragment() {

    private var _binding: FragmentChatIaBinding? = null
    private val binding get() = _binding!!

    private var eventos = "Você é um assistente"

    private var messageAdapter: MessageAdapter?=null

    private val firestore = FirebaseFirestore.getInstance()

    private var currentUser = FirebaseAuth.getInstance().currentUser

    private val chatCollectionRef = firestore.collection(currentUser?.email!!)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatIaBinding.inflate(inflater, container, false)
        messageAdapter = MessageAdapter(mutableListOf(), currentUser?.displayName!!)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = messageAdapter

        loadMessages(false)

        binding.messageInput.setEndIconOnClickListener {
            val messageText = binding.messageInputText.text.toString()
            if (messageText.isNotEmpty()) {
                Log.d("NameUser", currentUser?.displayName.toString())
                saveMessages(MessageDto(currentUser?.displayName.toString(), messageText),false)
                binding.messageInputText.text?.clear()
                send(messageText)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun send(prompt: String) {
        val url = "https://api.openai.com/v1/chat/completions"
        val apiKey = "sk-KCi9J_ZRAwbP0j7quEU7u-X3dPd85fHfykAr3YLkplT3BlbkFJIsDEI8JSuT2ztk6orRr9IoM6iu1wMX5kaSJltkNooA"
        Log.d("Prompt", prompt)

        val json = JSONObject()
        try {
            json.put("model", "gpt-3.5-turbo")

            val system = JSONObject().apply {
                put("role", "system")
                put("content", eventos)
            }

            val user = JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            }

            val messagesArray = JSONArray().apply {
                put(system)
                put(user)
            }

            json.put("messages", messagesArray)
        } catch (e: Exception) {
            Log.d("Error", e.toString())
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
                Log.d("Firestore", "json: $jsonObject")
                val content = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                Log.d("content",content)
                Log.d("content", content)
                Handler(Looper.getMainLooper()).post {
                    saveMessages(MessageDto("GPT", content),true)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("api-gpt", e.message.toString())
            }
        })
    }

    private fun saveMessages(message: MessageDto, isLast: Boolean) {
        chatCollectionRef.add(message)
            .addOnSuccessListener {
                Log.d("Firestore", "Mensagem salva com sucesso!")
                loadMessages(isLast)
            }
            .addOnFailureListener { e -> Log.w("Firestore", "Erro ao salvar mensagem", e) }
    }

    private fun loadMessages(isLast: Boolean) {
        chatCollectionRef.orderBy("timestamp").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }

            val messagesList = mutableListOf<MessageDto>()
            for (doc in snapshots!!) {
                val message = doc.toObject(MessageDto::class.java)
                messagesList.add(message)
            }

            Log.d("Firestore", "Mensagens carregadas: ${messagesList.size}")
            messageAdapter?.updateMessages(messagesList,isLast)

            if (messagesList.isNotEmpty()) {
                binding.recyclerView.scrollToPosition(messagesList.size - 1)
            }
        }
    }
}
