package com.jjfs.android.composetestapp.business.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val employeeId: String = "",
    val employeeName: String = "",
    val locationId: String = "",
    val locationCode: String = "",
    @Transient val accessToken: String = "",
    val roles: Set<String> = emptySet()
)