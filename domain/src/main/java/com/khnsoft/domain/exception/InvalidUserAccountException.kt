package com.khnsoft.domain.exception

class InvalidUserAccountException(msg: String? = null) : Exception(msg ?: "Invalid user account.")