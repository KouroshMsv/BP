package com.parvanpajooh.baseapp.models

import com.google.gson.annotations.SerializedName

data class UpdateModel (
        @SerializedName("latestVersion")
        val latestVersion: String,
        @SerializedName("latestVersionCode")
        val latestVersionCode: Int,
        @SerializedName("url")
        val url: String,
        @SerializedName("required")
        val required: Boolean,
        @SerializedName("releaseNotes")
        val releaseNotes: List<String>
    )