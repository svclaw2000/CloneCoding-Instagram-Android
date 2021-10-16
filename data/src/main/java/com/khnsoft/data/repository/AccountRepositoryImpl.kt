package com.khnsoft.data.repository

import com.khnsoft.data.repository.remote.AccountRemoteDataSource
import com.khnsoft.data.repository.remote.AccountRemoteDataSourceImpl
import com.khnsoft.domain.model.UserData
import com.khnsoft.domain.repository.AccountRepository
import kotlinx.coroutines.suspendCancellableCoroutine

class AccountRepositoryImpl(
    private val dataSource: AccountRemoteDataSource = AccountRemoteDataSourceImpl()
) : AccountRepository {
    override suspend fun registerWithEmail(email: String, password: String, account: UserData): Result<UserData> =
        suspendCancellableCoroutine { continuation ->
            dataSource.registerWithEmail(email, password, account, continuation)
        }

    override suspend fun loginWithEmail(email: String, password: String): Result<UserData> =
        suspendCancellableCoroutine { continuation ->
            dataSource.loginWithEmail(email, password, continuation)
        }
}