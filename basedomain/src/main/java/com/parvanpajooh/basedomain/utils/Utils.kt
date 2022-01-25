package com.parvanpajooh.basedomain.utils

import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import java.util.*

val username: String
    get() = PrefHelper.get<String?>(BasePrefKey.USERNAME.name, null) ?: throw NullPointerException("username is null")

fun String?.space() = if (this == null) "" else " "
val String.capitalize: String
    get() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }