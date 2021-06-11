package com.example.musicapp.data

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseStorage {
    var storageRef = FirebaseStorage.getInstance().reference

    fun getAll() = storageRef.listAll()
    fun getSimilar(p: String) = storageRef.child(p).listAll()
}
