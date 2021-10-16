package com.khnsoft.data.repository.remote

import com.khnsoft.domain.model.UserData
import kotlinx.coroutines.CancellableContinuation

interface AccountRemoteDataSource {

    fun registerWithEmail(
        email: String,
        password: String,
        userData: UserData,
        continuation: CancellableContinuation<Result<UserData>>
    )

    fun loginWithEmail(email: String, password: String, continuation: CancellableContinuation<Result<UserData>>)

}