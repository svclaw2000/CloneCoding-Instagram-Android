package com.khnsoft.data.repository.remote

import com.khnsoft.data.repository.listener.OnCanceledListener
import com.khnsoft.data.repository.listener.OnFailureListener
import com.khnsoft.data.repository.listener.OnSuccessListener
import com.khnsoft.domain.model.UserData

interface AccountRemoteDataSource {

    fun signupAccountWithEmail(
        email: String,
        password: String,
        onSuccessListener: OnSuccessListener<String>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    )

    fun loginWithEmail(
        email: String,
        password: String,
        onSuccessListener: OnSuccessListener<String>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    )

    fun setUserData(
        uid: String,
        userData: UserData,
        onSuccessListener: OnSuccessListener<UserData>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    )

    fun getUserData(
        uid: String,
        onSuccessListener: OnSuccessListener<UserData>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    )

}