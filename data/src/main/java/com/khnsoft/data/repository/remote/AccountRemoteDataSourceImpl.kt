package com.khnsoft.data.repository.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.khnsoft.data.api.ApiClient
import com.khnsoft.data.mapper.localToRemote
import com.khnsoft.data.mapper.remoteToLocal
import com.khnsoft.data.model.UserDataEntity
import com.khnsoft.data.repository.listener.OnCanceledListener
import com.khnsoft.data.repository.listener.OnFailureListener
import com.khnsoft.data.repository.listener.OnSuccessListener
import com.khnsoft.domain.exception.DataConvertException
import com.khnsoft.domain.exception.DuplicateEmailException
import com.khnsoft.domain.exception.LoginSessionException
import com.khnsoft.domain.model.UserData
import kotlin.coroutines.resume

class AccountRemoteDataSourceImpl(
    private val authApi: FirebaseAuth = ApiClient.authApi,
    private val firestoreApi: FirebaseFirestore = ApiClient.firestoreApi,
) : AccountRemoteDataSource {
    override fun signupAccountWithEmail(
        email: String,
        password: String,
        onSuccessListener: OnSuccessListener<String>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    ) {
        authApi.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult?.user?.uid?.let { uid ->
                    onSuccessListener.onSuccess(uid)
                } ?: onFailureListener.onFailure(LoginSessionException("User is not logged in after register."))
            }
            .addOnFailureListener { e ->
                when (e) {
                    is FirebaseAuthUserCollisionException ->
                        onFailureListener.onFailure(DuplicateEmailException(e.message))
                    else -> onFailureListener.onFailure(Exception("Exception occurs in register account."))
                }
            }
            .addOnCanceledListener {
                onCanceledListener.onCanceled()
            }
    }

    override fun loginWithEmail(
        email: String,
        password: String,
        onSuccessListener: OnSuccessListener<String>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    ) {
        authApi.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult?.user?.uid?.let { uid ->
                    onSuccessListener.onSuccess(uid)
                } ?: onFailureListener.onFailure(LoginSessionException("Cannot get uid of current user."))
            }
            .addOnFailureListener {
                onFailureListener.onFailure(LoginSessionException("Cannot login with email and password."))
            }
            .addOnCanceledListener {
                onCanceledListener.onCanceled()
            }
    }

    override fun setUserData(
        uid: String,
        userData: UserData,
        onSuccessListener: OnSuccessListener<UserData>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    ) {
        firestoreApi.collection(COL_USER_INFO)
            .document(uid)
            .set(localToRemote(userData))
            .addOnSuccessListener {
                onSuccessListener.onSuccess(userData)
            }
            .addOnFailureListener { e ->
                onFailureListener.onFailure(Exception("Exception occurs in register user account data."))
            }
            .addOnCanceledListener {
                onCanceledListener.onCanceled()
            }
    }

    override fun getUserData(
        uid: String,
        onSuccessListener: OnSuccessListener<UserData>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    ) {
        firestoreApi.collection(COL_USER_INFO)
            .document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.toObject(UserDataEntity::class.java)?.let { entity ->
                    onSuccessListener.onSuccess(remoteToLocal(entity))
                } ?: onFailureListener.onFailure(DataConvertException())
            }
            .addOnFailureListener {
                onFailureListener.onFailure(LoginSessionException("Cannot get data of current user."))
            }
            .addOnCanceledListener {
                onCanceledListener.onCanceled()
            }
    }

    companion object {
        private const val COL_USER_INFO = "userInfo"
    }
}