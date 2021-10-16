package com.khnsoft.domain.exception

class RequestCanceledException(msg: String? = null) : Exception(msg ?: "Request has been canceled.")