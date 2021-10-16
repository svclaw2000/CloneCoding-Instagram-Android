package com.khnsoft.data.repository.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.khnsoft.data.api.ApiClient
import com.khnsoft.data.mapper.localToRemote
import com.khnsoft.data.mapper.remoteToLocal
import com.khnsoft.data.model.UserDataEntity
import com.khnsoft.domain.exception.*
import com.khnsoft.domain.model.UserData
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

class AccountRemoteDataSourceImpl(
    private val authApi: FirebaseAuth = ApiClient.authApi,
    private val firestoreApi: FirebaseFirestore = ApiClient.firestoreApi,
) : AccountRemoteDataSource {
    override fun registerWithEmail(
        email: String,
        password: String,
        userData: UserData,
        continuation: CancellableContinuation<Result<UserData>>
    ) {
        authApi.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user?.uid?.let { uid ->
                    setUserData(uid, userData, continuation)
                } ?: continuation.resume(
                    Result.failure(LoginSessionException("User is not logged in after register."))
                )
            }
            .addOnFailureListener { e ->
                when (e) {
                    is FirebaseAuthUserCollisionException -> continuation.resume(
                        Result.failure(DuplicateEmailException(e.message))
                    )
                    else -> continuation.resume(
                        Result.failure(Exception("Exception occurs in register account."))
                    )
                }
            }
            .addOnCanceledListener {
                continuation.resume(Result.failure(RequestCanceledException()))
            }
    }

    private fun setUserData(
        uid: String,
        userData: UserData,
        continuation: CancellableContinuation<Result<UserData>>
    ) {
        firestoreApi.collection(COL_USER_INFO)
            .document(uid)
            .set(localToRemote(userData))
            .addOnSuccessListener {
                continuation.resume(Result.success(userData))
            }
            .addOnFailureListener {
                continuation.resume(
                    Result.failure(Exception("Exception occurs in register user account data."))
                )
            }
            .addOnCanceledListener {
                continuation.resume(Result.failure(RequestCanceledException()))
            }
    }

    override fun loginWithEmail(
        email: String,
        password: String,
        continuation: CancellableContinuation<Result<UserData>>
    ) {
        authApi.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user?.uid?.let { uid ->
                    getUserData(uid, continuation)
                } ?: continuation.resume(
                    Result.failure(LoginSessionException("Cannot get uid of current user."))
                )
            }
            .addOnFailureListener {
                continuation.resume(
                    Result.failure(LoginSessionException("Cannot login with email and password."))
                )
            }
            .addOnCanceledListener {
                continuation.resume(Result.failure(RequestCanceledException()))
            }
    }

    private fun getUserData(uid: String, continuation: CancellableContinuation<Result<UserData>>) {
        firestoreApi.collection(COL_USER_INFO)
            .document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.toObject(UserDataEntity::class.java)?.let { entity ->
                    continuation.resume(Result.success(remoteToLocal(entity)))
                } ?: continuation.resume(
                    Result.failure(DataConvertException())
                )
            }
            .addOnFailureListener {
                continuation.resume(
                    Result.failure(LoginSessionException("Cannot get data of current user."))
                )
            }
    }

    companion object {
        private const val COL_USER_INFO = "userInfo"
    }
}