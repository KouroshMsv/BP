package com.parvanpajooh.basedata.net.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
        @SerialName("access_token") val accessToken: String = "",
        @SerialName("token_type") val tokenType: String = "",
        @SerialName("refresh_token") val refreshToken: String = "",
        @SerialName("expires_in") val expiresIn: Long = 0,
        @SerialName("scope") val scope: String = "",
        @SerialName("jti") val jti: String = ""
)