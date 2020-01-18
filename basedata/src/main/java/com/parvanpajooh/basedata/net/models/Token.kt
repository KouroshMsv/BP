package com.parvanpajooh.basedata.net.models

import com.google.gson.annotations.SerializedName


 data class Token(
        @SerializedName("access_token") val accessToken: String = "",
        @SerializedName("token_type") val tokenType: String = "",
        @SerializedName("refresh_token") val refreshToken: String = "",
        @SerializedName("expires_in") val expiresIn: Long = 0,
        @SerializedName("scope") val scope: String = "",
        @SerializedName("jti") val jti: String = ""
)