package com.khnsoft.domain.exception

class DuplicateEmailException(msg: String? = null): Exception(msg ?: "Email address is in use.")