package com.parvanpajooh.basedomain.utils

import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper

val username: String
    get() = PrefHelper.get(BasePrefKey.USERNAME.name)

fun String?.space() = if (this == null) "" else " "
