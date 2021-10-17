package com.khnsoft.data.repository.listener

fun interface OnSuccessListener<T> {
    fun onSuccess(result: T)
}