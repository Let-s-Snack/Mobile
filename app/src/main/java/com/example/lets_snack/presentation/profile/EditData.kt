package com.example.lets_snack.presentation.profile

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.children
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.dto.PersonDtoResponse
import com.example.lets_snack.data.remote.dto.PersonDtoResponseEmail
import com.example.lets_snack.data.remote.dto.PersonDtoUpdate
import com.example.lets_snack.data.remote.dto.RestrcitionID
import com.example.lets_snack.data.remote.dto.RestrictionsDto
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository
import com.example.lets_snack.data.remote.repository.rest.RestrictionsRepository
import com.example.lets_snack.databinding.ActivityEditDataBinding
import com.example.lets_snack.presentation.Notification
import com.example.lets_snack.presentation.profile.photo.PhotoEditData
import com.example.lets_snack.presentation.transform.RoundedTransformation
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.squareup.picasso.Picasso

class EditData : AppCompatActivity() {
    private lateinit var binding: ActivityEditDataBinding
    private val restrictionsRepository = RestrictionsRepository()
    private val personsRepository = PersonsRepository(this)
    private var chipGroup: ChipGroup? = null
    private var currentPhoto:String? = null
    private var restrictionsArray:  ArrayList<String> =  ArrayList()
    private var restrictionListId: MutableList<RestrcitionID?> = mutableListOf()
    private var restrictionListObject: List<RestrictionsDto>? = listOf()
    private lateinit var adapterTypeRestrictions: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val typeRestriction = binding.typeRestrictionInput
        chipGroup = binding.chipGroup
        adapterTypeRestrictions = ArrayAdapter(this, android.R.layout.simple_list_item_checked, restrictionsArray)
        typeRestriction.setAdapter(adapterTypeRestrictions)
        typeRestriction.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        val call = restrictionsRepository.getRestrictions()
        binding.circlePencil.setOnClickListener {
            finish()
            startEditPhoto()
        }
        call.enqueue(object : retrofit2.Callback<List<RestrictionsDto>> {
            override fun onResponse(
                call: retrofit2.Call<List<RestrictionsDto>>,
                response: retrofit2.Response<List<RestrictionsDto>>
            ) {
                restrictionListObject = response.body()
                val restrictionsList: List<String>? = response.body()?.map { it.name }
                restrictionsList?.let {
                    restrictionsArray.clear()
                    restrictionsArray.addAll(it)
                    adapterTypeRestrictions.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<RestrictionsDto>>, t: Throwable) {
                Log.e("CallRestrictionsError", t.message.toString())
            }
        })

        typeRestriction.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if (!chipExists(selectedItem, chipGroup!!)) {
                addChipToGroup(selectedItem, chipGroup!!)
            }
            typeRestriction.text.clear()
        }
        Log.d("CallEditData", "email: "+FirebaseAuth.getInstance().currentUser?.email.toString())
        val callPersons = personsRepository.listPersonByEmail(FirebaseAuth.getInstance().currentUser?.email.toString())
        callPersons.enqueue(object : retrofit2.Callback<PersonDtoResponseEmail> {
            override fun onResponse(
                call: retrofit2.Call<PersonDtoResponseEmail>,
                response: retrofit2.Response<PersonDtoResponseEmail>
            ) {
                if(response.code() == 200) {
                    currentPhoto = response.body()?.urlPhoto
                    binding.usernameInput.setText(response.body()?.nickname ?: "")
                    binding.nameInput.setText(response.body()?.name ?: "")
                    binding.emailInput.setText(response.body()?.email ?: "")
                    val listRestrictions = response.body()?.restrictions
                    listRestrictions?.forEach { restriction ->
                        val chip = Chip(this@EditData).apply {
                            text =restriction?.name
                            isCheckable = true
                            isClickable = true
                            isCloseIconVisible = true
                            setChipBackgroundColorResource(R.color.laranja_pastel)
                            setOnCloseIconClickListener {
                                binding.chipGroup.removeView(this)
                            }
                        }
                        binding.chipGroup.addView(chip)
                    }
                    binding.genderInputText.setText(response.body()?.gender ?: "")
                }
                Log.d("CallEditData", response.code().toString())
            }

            override fun onFailure(call: retrofit2.Call <PersonDtoResponseEmail>, t: Throwable) {
                Log.e("CallEditDataError", t.message.toString())
            }
        })
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            if (currentUser.photoUrl != null) {

                Picasso.get()
                    .load(currentUser.photoUrl)
                    .resize(300, 300)
                    .centerCrop() // Centraliza e corta a imagem
                    .placeholder(R.drawable.profile_default)
                    .transform(RoundedTransformation(180, 0))
                    .into(binding.imageProfile)
            } else {
                binding.imageProfile.setImageResource(R.drawable.profile_default)
            }

        }

        binding.loginEnterBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.loginEnterBtn.text = ""
            binding.loginEnterBtn.isEnabled = false
            restrictionListObject.let {
                val chipSelected = chipGroup?.children?.filterIsInstance<Chip>()
                    ?.map {
                        it.text.toString()
                    }
                it?.filter { item ->
                    if (chipSelected?.contains(item.name) == true) {
                        restrictionListId.add(RestrcitionID(item.id))
                        true  // Retorna `true` para manter esse item no filtro, caso você precise
                    } else {
                        false
                    }
                }
            }
            val imageUrl = intent.getStringExtra("imageUrl") ?: currentPhoto!!
            Log.d("restrictionListId", restrictionListId.toString())
            Log.d("Name", binding.nameInput.text.toString())
            Log.d("Username", binding.usernameInput.text.toString())
            Log.d("Email", binding.emailInput.text.toString())
            Log.d("Gender", binding.genderInputText.text.toString())
            Log.d("ImageUrl", imageUrl)
            val personDto = PersonDtoUpdate(
                binding.genderInputText.text.toString(),
                binding.nameInput.text.toString(),
                binding.usernameInput.text.toString(),
                binding.emailInput.text.toString(),
                imageUrl,
                restrictionListId
            )
            valideAll(personDto.name, personDto.nickname) { isValid ->
                if (isValid) {
                    // Se todos os dados são válidos, tenta atualizar o MongoDB
                    updateMongo(personDto) { mongoSuccess ->
                        if (mongoSuccess) {
                            // Se a atualização no MongoDB foi bem-sucedida, atualiza o Firebase
                            updateFirebase(personDto) { firebaseSuccess ->
                                if (firebaseSuccess) {
                                    // Se a atualização no Firebase foi bem-sucedida, envia a notificação
                                    notification(personDto.nickname)
                                    binding.progressBar.visibility = View.INVISIBLE
                                    binding.loginEnterBtn.text = "Salvar"
                                    binding.loginEnterBtn.isEnabled = true
                                } else {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    binding.loginEnterBtn.text = "Salvar"
                                    binding.loginEnterBtn.isEnabled = true
                                    Toast.makeText(this, "Ops! Algo deu errado", Toast.LENGTH_SHORT).show()
                                    Log.d("FirebaseUpdate", "Erro na atualização no Firebase.")
                                }
                            }
                        } else {
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.loginEnterBtn.text = "Salvar"
                            binding.loginEnterBtn.isEnabled = true
                            Toast.makeText(this, "Ops! Algo deu errado", Toast.LENGTH_SHORT).show()
                            Log.d("MongoUpdate", "Erro na atualização no MongoDB.")
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.loginEnterBtn.text = "Salvar"
                    binding.loginEnterBtn.isEnabled = true
                    Log.d("Validation", "Falha na validação dos dados.")
                }
            }

            // Limpa a lista de restrições, independentemente do sucesso das atualizações
            restrictionListId.clear()
        }
    }

    private fun chipExists(text: String, chipGroup: ChipGroup): Boolean {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.text == text) {
                return true
            }
        }
        return false
    }

    private fun addChipToGroup(text: String, chipGroup: ChipGroup) {
        val chip = Chip(this).apply {
            setText(text)
            isCloseIconVisible = true
            setChipBackgroundColorResource(R.color.laranja_pastel)
            setOnCloseIconClickListener {
                chipGroup.removeView(this)
            }
        }
        chipGroup.addView(chip)
    }

    private fun startEditPhoto(){
        val intent = Intent(this, PhotoEditData::class.java)
        startActivity(intent)
    }

    private fun updateMongo(personDto: PersonDtoUpdate, callback: (Boolean) -> Unit) {
        val call = personsRepository.updatePerson(personDto.email, personDto)
        call.enqueue(object : retrofit2.Callback<PersonDtoResponse> {
            override fun onResponse(
                call: retrofit2.Call<PersonDtoResponse>,
                response: retrofit2.Response<PersonDtoResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse?.message == "Atualização feita com sucesso!") {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    Log.e("EditData", "Erro na resposta: ${response.errorBody()?.string()}")
                    callback(false)
                }
            }

            override fun onFailure(call: retrofit2.Call<PersonDtoResponse>, t: Throwable) {
                Log.e("EditData", "Erro na chamada: ${t.message}")
                callback(false)
            }
        })
    }

    private fun updateFirebase(personDto: PersonDtoUpdate, callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            user.updateEmail(personDto.email)
                .addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(personDto.nickname)
                            .setPhotoUri(Uri.parse(personDto.urlPhoto))
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    callback(true)
                                } else {
                                    Log.w("FirebaseUpdate", "Erro ao atualizar perfil", profileTask.exception)
                                    callback(false)
                                }
                            }
                    } else {
                        Log.w("FirebaseUpdate", "Erro ao atualizar o e-mail", emailTask.exception)
                        callback(false)
                    }
                }
        } else {
            Log.w("FirebaseUpdate", "Nenhum usuário autenticado.")
            callback(false)
        }
    }

    private fun notification(username: String){
        val intentAndroid = Intent(this, Notification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this,0,intentAndroid, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this,"channel_id")
            .setSmallIcon(R.drawable.icontext_lets_snack)
            .setContentTitle("Let's Snack")
            .setContentText("Parabéns ${username}, seu perfil foi alterado com sucesso!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val channel = NotificationChannel("channel_id", "Let's Snack" +
                "", NotificationManager.IMPORTANCE_HIGH)

        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManagerCompat.notify(1, builder.build())
    }

    private fun valideName(name:String):Boolean {
        if (name.isEmpty()) {
            binding.textInputLayout.error = "Nome deve ter pelo menos 1 caractere"
            return false
        } else if (name.length > 45) {
            binding.textInputLayout.error = "Nome não pode ter mais que 45 caracteres"
            return false
        }
        else{
            binding.textInputLayout.error = null
            return true
        }
    }

    fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
        val call = personsRepository.listPersonByUsername(username)
        call.enqueue(object : retrofit2.Callback<PersonDtoResponse> {
            override fun onResponse(
                call: retrofit2.Call<PersonDtoResponse>,
                response: retrofit2.Response<PersonDtoResponse>
            ) {
                val usernameExists = if (response.isSuccessful) {
                    val apiResponse = response.body()

                    // Verifica se a mensagem indica que o usuário não existe ou se é o mesmo usuário logado
                    apiResponse?.message != "Apelido do usuário não existe" &&
                            apiResponse?.email != FirebaseAuth.getInstance().currentUser?.email
                } else {
                    // Em caso de erro na resposta, considerar que o nome de usuário existe
                    true
                }

                callback(usernameExists)
            }

            override fun onFailure(call: retrofit2.Call<PersonDtoResponse>, t: Throwable) {
                Log.e("Username Check", "Erro na chamada: ${t.message}")
                callback(true) // Em caso de falha, considerar que o nome de usuário existe
            }
        })
    }


    private fun valideUserName(username:String):Boolean {
        if (username.isEmpty()) {
            binding.textInputLayout3.error = "Username deve ter pelo menos 1 caractere"
            return false
        } else if (username.length > 200) {
            binding.textInputLayout3.error = "Username não pode ter mais que 200 caracteres"
            return false
        }
        else{
            binding.textInputLayout3.error = null
            return true
        }
    }

    private fun valideEmail() {
        val email = binding.emailInput.text.toString()
        if (!isEmailValid(email)) {
            binding.textInputLayoutEmailLogin.error = "E-mail inválido"
        } else {
            binding.textInputLayoutEmailLogin.error = null
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun valideAll(name: String, username: String, callback: (Boolean) -> Unit) {
        // Valida o nome
        val isNameValid = valideName(name)

        // Valida o username
        val isUsernameValid = valideUserName(username)


// Verifica a disponibilidade do username
        checkUsernameAvailability(username) { usernameExists ->
            if (usernameExists) {
                // Nome de usuário já existe
                binding.textInputLayout3.error = "Já existe um usuário com esse nome de usuário."
                callback(false)
            } else {
                // Nome de usuário está disponível, limpa o erro, e continua a validação do email
                binding.textInputLayout3.error = null

                // Valida o email
                val email = binding.emailInput.text.toString()
                valideEmail() // Atualiza o erro no campo se necessário

                // Verifica se o formato do email é válido
                val isEmailValid = isEmailValid(email)
                if (!isEmailValid) {
                    // Email é inválido
                    binding.textInputLayoutEmailLogin.error = "E-mail inválido"
                    callback(false)
                } else {
                    // Email no formato correto, limpa o erro e verifica se já existe
                    binding.textInputLayoutEmailLogin.error = null

                    validateEmailExists(email) { emailExists ->
                        if (emailExists) {
                            // Email já está em uso
                            binding.textInputLayoutEmailLogin.error = "E-mail já está em uso."
                            callback(false)
                        } else {
                            // Tudo válido: nome de usuário e email estão disponíveis e no formato correto
                            binding.textInputLayoutEmailLogin.error = null
                            callback(isNameValid && isUsernameValid && isEmailValid && !usernameExists && !emailExists)
                        }
                    }
                }
            }
        }

    }


    private fun validateEmailExists(email: String, callback: (Boolean) -> Unit) {
        val callPersons = personsRepository.listPersonByEmail(email)
        var exists: Boolean? = null
        callPersons.enqueue(object : retrofit2.Callback<PersonDtoResponseEmail> {
            override fun onResponse(
                call: retrofit2.Call<PersonDtoResponseEmail>,
                response: retrofit2.Response<PersonDtoResponseEmail>
            ) {
                if (response.isSuccessful && response.code() == 200) {
                    if(response.body()?.email != FirebaseAuth.getInstance().currentUser?.email){
                        exists =true
                    }
                    exists = false
                } else {
                    exists =true
                }

                Log.d("CallEditData", response.code().toString())
                Log.d("CallEditData", exists.toString())
                callback(exists!!)
            }

            override fun onFailure(call: retrofit2.Call<PersonDtoResponseEmail>, t: Throwable) {
                Log.e("CallEditDataError", t.message.toString())
                callback(true) // Assume que o email não existe em caso de erro
            }
        })
    }


}