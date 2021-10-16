package com.khnsoft.domain.exception

class LoginSessionException(msg: String? = null) : Exception(msg ?: "Invalid login session.")