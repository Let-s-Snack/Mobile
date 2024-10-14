package com.example.lets_snack.data.remote.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream

class DataBase {

    fun uploadFotoToFirebaseStorage(
        context: Context,
        imageUri: Uri,
        docData: MutableMap<String, String>,
        onUploadSuccess: (String) -> Unit
    ) {
        val storageRef = LetsSnackFirebaseBuilder.storage
            .getReference("galeria")
            .child("fotoLinda" + System.currentTimeMillis() + ".jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Recupera a URL da imagem salva no Firebase Storage
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    // Adiciona a URL da imagem ao map docData
                    val imageUrl = downloadUri.toString()
                    docData["uri"] = imageUrl
                    onUploadSuccess(imageUrl)  // Retorna a URL de sucesso
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Falha ao salvar imagem no Firebase", Toast.LENGTH_SHORT).show()
            }
    }

}