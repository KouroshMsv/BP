package com.parvanpajooh.baseapp.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateModel (
        @SerialName("latestVersion")
        val latestVersion: String,
        @SerialName("latestVersionCode")
        val latestVersionCode: Int,
        @SerialName("url")
        val url: String,
        @SerialName("required")
        val required: Boolean,
        @SerialName("releaseNotes")
        val releaseNotes: List<String>
    )