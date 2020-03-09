package com.parvanpajooh.basedomain.utils.sharedpreferences

import android.content.Context
import com.orhanobut.hawk.Hawk

object PrefHelper {
    fun init(context: Context) = Hawk.init(context).build()

    fun contains(key: String) = Hawk.contains(key)

    fun <T> put(key: String, value: T) = Hawk.put(key, value)

    fun <T> get(key: String): T = Hawk.get(key)

    fun <T> get(key: String, defaultValue: T) = Hawk.get(key, defaultValue)

    fun delete(key: String) = Hawk.delete(key)

    fun deleteAll() = Hawk.deleteAll()

    fun count() = Hawk.count()

}