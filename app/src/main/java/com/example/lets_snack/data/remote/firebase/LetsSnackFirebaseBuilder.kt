package com.example.lets_snack.data.remote.firebase

import com.google.firebase.storage.FirebaseStorage

class LetsSnackFirebaseBuilder {
    companion object{
        val storage = FirebaseStorage.getInstance()
    }
}