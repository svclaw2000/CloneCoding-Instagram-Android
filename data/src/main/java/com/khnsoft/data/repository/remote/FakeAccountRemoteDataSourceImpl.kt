package com.khnsoft.data.repository.remote

import com.khnsoft.data.repository.listener.OnCanceledListener
import com.khnsoft.data.repository.listener.OnFailureListener
import com.khnsoft.data.repository.listener.OnSuccessListener
import com.khnsoft.domain.exception.DuplicateEmailException
import com.khnsoft.domain.exception.LoginSessionException
import com.khnsoft.domain.model.UserData

class FakeAccountRemoteDataSourceImpl : AccountRemoteDataSource {
    override fun signupAccountWithEmail(
        email: String,
        password: String,
        onSuccessListener: OnSuccessListener<String>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    ) {
        when (email) {
            "success@email.com" -> onSuccessListener.onSuccess(UID)
            else -> onFailureListener.onFailure(DuplicateEmailException())
        }
    }

    override fun loginWithEmail(
        email: String,
        password: String,
        onSuccessListener: OnSuccessListener<String>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    ) {
        when (email) {
            "success@email.com" -> onSuccessListener.onSuccess(UID)
            else -> onFailureListener.onFailure(LoginSessionException())
        }
    }

    override fun setUserData(
        uid: String,
        userData: UserData,
        onSuccessListener: OnSuccessListener<UserData>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    ) {
        onSuccessListener.onSuccess(userData)
    }

    override fun getUserData(
        uid: String,
        onSuccessListener: OnSuccessListener<UserData>,
        onFailureListener: OnFailureListener,
        onCanceledListener: OnCanceledListener
    ) {
        onSuccessListener.onSuccess(USER_DATA)
    }

    companion object {
        const val UID = "12341234"
        val USER_DATA = UserData("", "", "", "")
    }
}