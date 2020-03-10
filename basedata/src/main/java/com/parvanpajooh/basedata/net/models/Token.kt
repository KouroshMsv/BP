package com.parvanpajooh.basedata.net.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(
        @SerialName("access_token") val access_token: String = "",
        @SerialName("token_type") val token_type: String = "",
        @SerialName("refresh_token") val refresh_token: String = "",
        @SerialName("expires_in") val expires_in: Long = 0,
        @SerialName("scope") val scope: String = "",
        @SerialName("jti") val jti: String = ""
)