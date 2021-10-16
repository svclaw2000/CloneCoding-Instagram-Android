package com.khnsoft.data.repository.remote

import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.khnsoft.data.model.UserDataEntity
import com.khnsoft.domain.exception.DuplicateEmailException
import com.khnsoft.domain.exception.LoginSessionException
import com.khnsoft.domain.model.UserData
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Assert.*
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AccountRemoteDataSourceImplTest {

    private lateinit var dataSource: AccountRemoteDataSource

    // auth

    @Mock
    private lateinit var mockAuthApi: FirebaseAuth

    @Mock
    private lateinit var mockAuthResultTask: Task<AuthResult>

    @Mock
    private lateinit var mockAuthResult: AuthResult

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Captor
    private lateinit var testOnAuthResultSuccessListener: ArgumentCaptor<OnSuccessListener<AuthResult>>

    // firestore

    @Mock
    private lateinit var mockFirestoreApi: FirebaseFirestore

    @Mock
    private lateinit var mockVoidTask: Task<Void>

    @Captor
    private lateinit var testOnVoidSuccessListener: ArgumentCaptor<OnSuccessListener<Void>>

    @Mock
    private lateinit var mockSnapshotTask: Task<DocumentSnapshot>

    @Captor
    private lateinit var testOnSnapshotSuccessListener: ArgumentCaptor<OnSuccessListener<DocumentSnapshot>>

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    @Captor
    private lateinit var testUserDataEntity: ArgumentCaptor<UserDataEntity>

    @Mock
    private lateinit var mockSnapshot: DocumentSnapshot

    private val userData = UserData("", "", "", "")
    private val userDataEntity = UserDataEntity("", "", "", "")

    // common

    @Captor
    private lateinit var testOnFailureListener: ArgumentCaptor<OnFailureListener>

    @Captor
    private lateinit var testOnCanceledListener: ArgumentCaptor<OnCanceledListener>

    // test

    @Before
    fun setup() {
        dataSource = AccountRemoteDataSourceImpl(mockAuthApi, mockFirestoreApi)

        setupTask(mockAuthResultTask, testOnAuthResultSuccessListener)
        setupTask(mockSnapshotTask, testOnSnapshotSuccessListener)
        setupTask(mockVoidTask, testOnVoidSuccessListener)

        `when`(mockAuthApi.createUserWithEmailAndPassword("", "")).thenReturn(mockAuthResultTask)
        `when`(mockAuthApi.signInWithEmailAndPassword("", "")).thenReturn(mockAuthResultTask)

        `when`(mockAuthResult.user).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn(UID)

        `when`(mockFirestoreApi.collection(COL_USER_INFO)).thenReturn(mockCollection)
        `when`(mockCollection.document(UID)).thenReturn(mockDocument)
        `when`(mockDocument.set(testUserDataEntity.capture())).thenReturn(mockVoidTask)
        `when`(mockDocument.get()).thenReturn(mockSnapshotTask)

        `when`(mockSnapshot.toObject(UserDataEntity::class.java)).thenReturn(userDataEntity)
    }

    private fun <T> setupTask(task: Task<T>, onSuccessListenerCaptor: ArgumentCaptor<OnSuccessListener<T>>) {
        `when`(task.addOnSuccessListener(onSuccessListenerCaptor.capture())).thenReturn(task)
        `when`(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task)
        `when`(task.addOnCanceledListener(testOnCanceledListener.capture())).thenReturn(task)
    }

    @Test
    fun registerWithEmail_newEmail_success(): Unit = runBlocking {
        val result = suspendCancellableCoroutine<Result<UserData>> { continuation ->
            dataSource.registerWithEmail(
                "",
                "",
                userData,
                continuation
            )

            testOnAuthResultSuccessListener.value.onSuccess(mockAuthResult)
            testOnVoidSuccessListener.value.onSuccess(null)
        }

        assertTrue(result.isSuccess)
        assertEquals(userData, result.getOrNull())
    }

    @Test
    fun registerWithEmail_existEmail_failure(): Unit = runBlocking {
        val result = suspendCancellableCoroutine<Result<UserData>> { continuation ->
            dataSource.registerWithEmail(
                "",
                "",
                userData,
                continuation
            )

            testOnFailureListener.value.onFailure(Exception())
        }

        assertTrue(result.isFailure)
    }

    @Test
    fun loginWithEmail_right_success(): Unit = runBlocking {
        val result = suspendCancellableCoroutine<Result<UserData>> { continuation ->
            dataSource.loginWithEmail("", "", continuation)

            testOnAuthResultSuccessListener.value.onSuccess(mockAuthResult)
            testOnSnapshotSuccessListener.value.onSuccess(mockSnapshot)
        }

        assertTrue(result.isSuccess)
        assertEquals(userData, result.getOrNull())
    }

    @Test
    fun loginWithEmail_wrong_failure(): Unit = runBlocking {
        val result = suspendCancellableCoroutine<Result<UserData>> { continuation ->
            dataSource.loginWithEmail("", "", continuation)

            testOnFailureListener.value.onFailure(Exception())
        }

        assertTrue(result.isFailure)
        assert(result.exceptionOrNull() is LoginSessionException)
    }

    companion object {
        private const val UID = "1234"
        private const val COL_USER_INFO = "userInfo"
    }
}