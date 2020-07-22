package com.parvanpajooh.basedomain.utils

import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.logW

val username: String?
    get() = PrefHelper.get(BasePrefKey.USERNAME.name, null)

fun String?.space() = if (this == null) "" else " "

fun <T> findUsername(
    notFoundUsername: () -> Unit = { logW("username is null") },
    foundUsername: (username: String) -> T
    ) {
    if (username == null) {
        notFoundUsername()
    } else {
        foundUsername(username!!)
    }
}
