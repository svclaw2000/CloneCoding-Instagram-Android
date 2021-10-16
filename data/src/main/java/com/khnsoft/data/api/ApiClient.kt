package com.khnsoft.data.api

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ApiClient {
    val authApi get() = Firebase.auth
    val firestoreApi get() = Firebase.firestore
}