package com.parvanpajooh.basedata.sync

import android.database.Cursor
import android.net.Uri
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper

internal interface SyncContract {
  companion object {

    val PICKUP_URI = Uri.parse("content://${PrefHelper.get<String>(BasePrefKey.AUTHORITY.name)}/pickup")

    fun getColumnString(cursor: Cursor, columnName: String): String {
      return cursor.getString(cursor.getColumnIndex(columnName))
    }

    fun getColumnInt(cursor: Cursor, columnName: String): Int {
      return cursor.getInt(cursor.getColumnIndex(columnName))
    }

    fun getColumnLong(cursor: Cursor, columnName: String): Long {
      return cursor.getLong(cursor.getColumnIndex(columnName))
    }
  }

}
