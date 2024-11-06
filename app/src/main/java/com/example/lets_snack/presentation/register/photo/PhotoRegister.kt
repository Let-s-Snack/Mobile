package com.example.lets_snack.presentation.register.photo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.firebase.DataBase
import com.example.lets_snack.presentation.register.restriction.RestrictionRegister
import com.example.lets_snack.databinding.ActivityRegisterPhotoBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Suppress("NAME_SHADOWING")
class PhotoRegister : AppCompatActivity() {
    private var docData: MutableMap<String, String> = mutableMapOf()
    private lateinit var binding: ActivityRegisterPhotoBinding
    private lateinit var photo: ImageView
    private lateinit var buttonRegister: Button
    private val database = DataBase()
    private var imageUri: Uri? = null // Variável para armazenar o URI da imagem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buttonRegister = binding.loginEnterBtn
        photo = binding.photo
        Glide.with(this)
            .load(R.drawable.profile_default)
            .circleCrop()
            .into(photo)
        photo.visibility = ImageView.VISIBLE
        val galleryBtn = binding.galleryBtn

        galleryBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncherGallery.launch(intent)
        }

        val photoBtn = binding.photoBtn

        photoBtn.setOnClickListener {
            resultLauncherCamera.launch(null)
        }

        // Configura o clique do botão de registro
        buttonRegister.setOnClickListener {
            imageUri?.let {
                binding.progressBar.visibility = View.VISIBLE;
                binding.loginEnterBtn.text = ""
                binding.loginEnterBtn.isEnabled = false;
                // Chama o método para fazer upload da imagem no Firebase Storage
                database.uploadFotoToFirebaseStorage(this, it, docData) { downloadUrl ->
                    binding.progressBar.visibility = View.GONE;
                    binding.loginEnterBtn.text = "PRÓXIMO"
                    binding.loginEnterBtn.isEnabled = true;
                    Log.d("FirebaseURL", "Imagem salva com sucesso: $downloadUrl")
                    startRestrictionRegister(Uri.parse(downloadUrl)) // Passa a URL para a próxima tela
                }
            }
        }
    }

    private val resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                imageUri = intent?.data
                if (imageUri != null) {
                    buttonRegister.isEnabled = true // Habilita o botão de registro

                    Glide.with(this)
                        .load(imageUri)
                        .circleCrop()
                        .into(photo)

                    photo.visibility = ImageView.VISIBLE
                    docData["uri"] = imageUri.toString()
                }
            }
        }

    private fun startRestrictionRegister(photo: Uri) {
        val bundle = intent.getBundleExtra("bundleRegister")
        val intent = Intent(this, RestrictionRegister::class.java)
        bundle?.putString("photo", photo.toString())
        if (bundle != null) {
            val name = bundle.getString("name")
            val gender = bundle.getString("gender")
            val dateOfBirth = bundle.getString("dateOfBirth")
            val username = bundle.getString("username")
            val email = bundle.getString("email")
            val phone = bundle.getString("phone")
            val password = bundle.getString("password")
            val photo = bundle.getString("photo")

            Log.e("BundlePhoto", "Name: $name, Gender: $gender, Date of Birth: $dateOfBirth, Username: $username, Email: $email, Phone: $phone, Password: $password, Photo: $photo")
        } else {
            Log.e("Bundle", "Bundle is null")
        }
        intent.putExtra("bundleRegister", bundle)
        startActivity(intent)
    }

    private val resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                // Salva o Bitmap localmente e obtém o Uri
                imageUri = saveBitmapAndGetUri(this, bitmap)
                if (imageUri != null) {
                    buttonRegister.isEnabled = true // Habilita o botão de registro

                    Glide.with(this)
                        .load(imageUri)
                        .circleCrop()
                        .into(photo)

                    photo.visibility = ImageView.VISIBLE
                    docData["uri"] = imageUri.toString() // Adiciona a URI no map
                }
            }
        }

    fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
        val filename = "image_${System.currentTimeMillis()}.jpg"
        var uri: Uri? = null
        try {
            val fos: OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore para salvar a imagem no armazenamento externo
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri = imageUri
                fos = context.contentResolver.openOutputStream(imageUri!!)
            } else {
                // Para versões abaixo do Android Q
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
                uri = Uri.fromFile(image)
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri
    }

    fun finishScreen(view: View) {
        finish()
    }
}
