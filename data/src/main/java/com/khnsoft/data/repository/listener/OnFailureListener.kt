package com.khnsoft.data.repository.listener

fun interface OnFailureListener {
    fun onFailure(e: Exception)
}