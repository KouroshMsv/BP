package com.parvanpajooh.baseapp.models

data class UpdateModel (
        val latestVersion: String,
        val latestVersionCode: Int,
        val url: String,
        val required: Boolean,
        val releaseNotes: List<String>
    )