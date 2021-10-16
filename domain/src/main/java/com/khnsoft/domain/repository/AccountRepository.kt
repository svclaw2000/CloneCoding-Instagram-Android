package com.khnsoft.domain.repository

import com.khnsoft.domain.model.UserData

interface AccountRepository {

    suspend fun registerWithEmail(email: String, password: String, account: UserData): Result<UserData>

    suspend fun loginWithEmail(email: String, password: String): Result<UserData>

}