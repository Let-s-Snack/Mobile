package com.example.lets_snack.presentation.register.photo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.lets_snack.R
import com.example.lets_snack.presentation.register.restriction.RestrictionRegister
import com.example.lets_snack.databinding.ActivityRegisterPhotoBinding

@Suppress("NAME_SHADOWING")
class PhotoRegister : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPhotoBinding
    private lateinit var photo: ImageView
    private lateinit var buttonRegister: Button
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
    }

    private val resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val imageUri = intent?.data
                if (imageUri != null) {
                    buttonRegister.isEnabled = true
                    buttonRegister.setOnClickListener {
                        startRestrictionRegister(imageUri)
                    }
                    Glide.with(this)
                        .load(imageUri)
                        .circleCrop()
                        .into(photo)

                    photo.visibility = ImageView.VISIBLE
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
            buttonRegister.isEnabled = true
            buttonRegister.setOnClickListener {}
            if (bitmap != null) {
                Glide.with(this)
                    .load(bitmap)
                    .circleCrop()
                    .into(photo)

            }
        }
}