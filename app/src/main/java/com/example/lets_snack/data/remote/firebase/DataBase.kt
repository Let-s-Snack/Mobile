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
    fun uploadFoto(context: Context, foto: ImageView, docData: MutableMap<String, String>){
        val bitmap = (foto.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val dataByte = baos.toByteArray()

        val storage = LetsSnackFirebaseBuilder.storage
        storage.getReference("galeria").child("fotoLinda"+System.currentTimeMillis()+".jpg")
            .putBytes(dataByte).addOnSuccessListener {
                Toast.makeText(context, "Sucesso", Toast.LENGTH_SHORT).show()
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                    docData.put("uri", it.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Falha", Toast.LENGTH_SHORT).show()
            }

    }
    fun downloadFoto(img: ImageView, urlFirebase: Uri){
        img.rotation = 0f
        Glide.with(img.context).asBitmap().load(urlFirebase).into(img)
    }
}