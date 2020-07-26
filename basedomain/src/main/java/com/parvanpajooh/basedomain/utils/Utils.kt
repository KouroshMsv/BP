package com.parvanpajooh.basedomain.utils

import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.logW
import java.lang.NullPointerException

val username: String
    get() = PrefHelper.get(BasePrefKey.USERNAME.name, null)?:throw NullPointerException("username is null")

fun String?.space() = if (this == null) "" else " "
