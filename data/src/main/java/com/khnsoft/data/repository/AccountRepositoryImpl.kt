package com.khnsoft.data.repository

import com.khnsoft.data.repository.remote.AccountRemoteDataSource
import com.khnsoft.data.repository.remote.AccountRemoteDataSourceImpl
import com.khnsoft.domain.exception.RequestCanceledException
import com.khnsoft.domain.model.UserData
import com.khnsoft.domain.repository.AccountRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AccountRepositoryImpl(
    private val dataSource: AccountRemoteDataSource = AccountRemoteDataSourceImpl()
) : AccountRepository {
    override suspend fun registerWithEmail(email: String, password: String, userData: UserData): Result<UserData> =
        suspendCancellableCoroutine { continuation ->
            dataSource.signupAccountWithEmail(email, password, { uid ->
                dataSource.setUserData(uid, userData, { userData ->
                    continuation.resume(Result.success(userData))
                }, { e ->
                    continuation.resume(Result.failure(e))
                }, {
                    continuation.resume(Result.failure(RequestCanceledException()))
                })
            }, { e ->
                continuation.resume(Result.failure(e))
            }, {
                continuation.resume(Result.failure(RequestCanceledException()))
            })
        }

    override suspend fun loginWithEmail(email: String, password: String): Result<UserData> =
        suspendCancellableCoroutine { continuation ->
            dataSource.loginWithEmail(email, password, { uid ->
                dataSource.getUserData(uid, { userData ->
                    continuation.resume(Result.success(userData))
                }, { e ->
                    continuation.resume(Result.failure(e))
                }, {
                    continuation.resume(Result.failure(RequestCanceledException()))
                })
            }, { e ->
                continuation.resume(Result.failure(e))
            }, {
                continuation.resume(Result.failure(RequestCanceledException()))
            })
        }
}