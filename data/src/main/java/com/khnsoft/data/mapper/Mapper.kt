package com.khnsoft.data.mapper

import com.khnsoft.data.model.UserDataEntity
import com.khnsoft.domain.model.UserData

fun remoteToLocal(entity: UserDataEntity) =
    with(entity) {
        UserData(username, name, email, tel)
    }

fun localToRemote(item: UserData) =
    with(item) {
        UserDataEntity(username, name, email, tel)
    }