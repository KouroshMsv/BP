package com.parvanpajooh.basedomain.utils

import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.logW

private val username: String?
    get() = PrefHelper.get(BasePrefKey.USERNAME.name, null)

fun String?.space() = if (this == null) "" else " "

fun logUsernameIsNull(functionName:String) = logW("$functionName -> username is null")

fun <T> findUsername(
    foundUsername: (username: String) -> T,
    notFoundUsername: () -> T
): T {
    return if (username == null) {
        notFoundUsername()
    } else {
        foundUsername(username!!)
    }
}
suspend fun <T> findUsernameSuspend(
    foundUsername: suspend (username: String) -> T,
    notFoundUsername: suspend () -> T
): T {
    return if (username == null) {
        notFoundUsername()
    } else {
        foundUsername(username!!)
    }
}
