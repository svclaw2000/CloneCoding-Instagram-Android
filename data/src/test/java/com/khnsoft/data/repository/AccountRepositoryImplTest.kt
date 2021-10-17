package com.khnsoft.data.repository

import com.khnsoft.data.repository.remote.FakeAccountRemoteDataSourceImpl
import com.khnsoft.domain.exception.DuplicateEmailException
import com.khnsoft.domain.exception.LoginSessionException
import com.khnsoft.domain.repository.AccountRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Test

import org.junit.Assert.*

class AccountRepositoryImplTest {

    private val fakeDataSource = FakeAccountRemoteDataSourceImpl()
    private val repository: AccountRepository = AccountRepositoryImpl(fakeDataSource)

    @Test
    fun registerWithEmail_newEmail_success(): Unit = runBlocking {
        val result = repository.registerWithEmail("success@email.com", "", FakeAccountRemoteDataSourceImpl.USER_DATA)
        assertTrue(result.isSuccess)
    }

    @Test
    fun registerWithEmail_existEmail_failure(): Unit = runBlocking {
        val result = repository.registerWithEmail("duplicate@email.com", "", FakeAccountRemoteDataSourceImpl.USER_DATA)
        assertTrue(result.isFailure)
        assert(result.exceptionOrNull() is DuplicateEmailException)
    }

    @Test
    fun loginWithEmail_right_success(): Unit = runBlocking {
        val result = repository.loginWithEmail("success@email.com", "")
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun loginWithEmail_wrong_failure(): Unit = runBlocking {
        val result = repository.loginWithEmail("failure@email.com", "")
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}