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
import com.example.lets_snack.BuildConfig
import com.example.lets_snack.constants.LetsSnackConstants
import com.example.lets_snack.data.remote.dto.MessageDtoChat
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

        binding.addChat.setOnClickListener {
            Log.d("new Chat","criou")
            createNewChat(){
                loadMessages(true)
            }
        }
        loadMessages(false)
        checkAndCreateChatIfNeeded()

        binding.messageInput.setEndIconOnClickListener {
            val messageText = binding.messageInputText.text.toString()
            if (messageText.isNotEmpty()) {
                Log.d("NameUser", currentUser?.displayName.toString())
                saveMessages(MessageDtoChat(currentUser?.displayName.toString(), messageText),false)
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

    private fun checkAndCreateChatIfNeeded() {
        val counterDocRef = firestore.collection("counters").document(currentUser?.email!!)

        counterDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d("Firestore", "Contador já existe, não precisa criar novo chat.")
                    loadMessages(false) // Apenas carrega as mensagens existentes
                } else {
                    Log.d("Firestore", "Contador não encontrado, criando novo chat.")
                    // Chama o método para criar o novo chat
                    createNewChat {
                        loadMessages(true) // Carrega as mensagens após a criação
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao acessar o documento do contador: ", exception)
                createNewChat {
                    loadMessages(true)
                }
            }
    }
    private fun send(prompt: String) {
        val url = "https://api.openai.com/v1/chat/completions"
        val apiKey = BuildConfig.API_KEY
        Log.d("Prompt", prompt)

        val json = JSONObject()
        try {
            json.put("model", "gpt-4o-2024-08-06")

            val system = JSONObject().apply {
                put("role", "system")
                put("content", LetsSnackConstants.CONTEXT_CHAT.value)
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
        Log.d("api-gpt", "${json.toString()}")
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
                    saveMessages(MessageDtoChat("GPT", content),true)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("api-gpt", e.message.toString())
            }
        })
    }


    private fun saveMessageToChat(currentCount: Long, message: MessageDtoChat, isLast: Boolean) {
        // Salva a mensagem no chat com base no contador atual
        chatCollectionRef.document("chat${currentCount - 1}").collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d("Firestore", "Mensagem salva com sucesso!")
                loadMessages(isLast)
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao salvar mensagem", e)
            }
    }

    private fun loadMessages(isLast: Boolean) {
        // Acessa o documento de contador na coleção 'counters'
        val counterDocRef = firestore.collection("counters").document(currentUser?.email!!)

        counterDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Obtém o valor do contador
                    val currentCount = documentSnapshot.getLong("count") ?: 0

                    // Acessa o documento correto do chat com base no valor do contador
                    chatCollectionRef.document("chat${currentCount - 1}").collection("messages")
                        .orderBy("timestamp")
                        .addSnapshotListener { snapshots, e ->
                            if (e != null) {
                                Log.w("Firestore", "Listen failed.", e)
                                return@addSnapshotListener
                            }

                            val messagesList = mutableListOf<MessageDtoChat>()
                            for (doc in snapshots!!) {
                                val message = doc.toObject(MessageDtoChat::class.java)
                                messagesList.add(message)
                            }

                            Log.d("Firestore", "Mensagens carregadas: ${messagesList.size}")
                            messageAdapter?.updateMessages(messagesList, isLast)

                            // Atualiza o RecyclerView com a nova lista de mensagens
                            if (messagesList.isNotEmpty()) {
                                // Garante que a rolagem para a última posição ocorra apenas se for a última mensagem
                                if (isLast) {
                                    binding.recyclerView.scrollToPosition(messagesList.size - 1)
                                }
                            }
                        }
                } else {
                    Log.w("Firestore", "Documento de contador não encontrado.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao acessar o documento do contador: ", exception)
            }
    }

    private fun saveMessages(message: MessageDtoChat, isLast: Boolean) {
        val counterDocRef = firestore.collection("counters").document(currentUser?.email!!)

        // Verifica se o documento do contador existe
        counterDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Se o contador existir, obtemos o valor e salvamos a mensagem no chat
                    val currentCount = documentSnapshot.getLong("count") ?: 0
                    saveMessageToChat(currentCount, message, isLast)
                } else {
                    // Caso o contador não exista, cria um novo chat e aguarda a criação antes de carregar as mensagens
                    createNewChat {
                        loadMessages(isLast)
                    }
                }
            }
            .addOnFailureListener { exception ->
                createNewChat {
                    loadMessages(isLast)
                }
                Log.e("Firestore", "Erro ao acessar o documento do contador: ", exception)
            }
    }

    private fun createNewChat(onChatCreated: () -> Unit) {
        val counterDocRef = firestore.collection("counters").document(currentUser?.email!!)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(counterDocRef)
            val currentCount = snapshot.getLong("count") ?: 0
            if (snapshot.exists()) {
                transaction.update(counterDocRef, "count", currentCount + 1)
            } else {
                transaction.set(counterDocRef, mapOf("count" to 1))
            }


                firestore.collection(currentUser?.email!!).document("chat${currentCount}")
                    .collection("messages").add(
                        MessageDtoChat("GPT", "Oi! Seja muito bem-vindo ao nosso chat! \uD83C\uDF4A\uD83D\uDC9B Estamos super felizes em ter você por aqui. Vamos conversar e compartilhar boas vibes!")
                    )
            currentCount + 1
        }
            .addOnSuccessListener {
                Log.d("Firestore", "Contador incrementado e chat criado com sucesso.")
                onChatCreated() // Chama o callback após a criação do chat
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao criar chat e atualizar contador: ", exception)
            }
    }


}
