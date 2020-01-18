package com.parvanpajooh.baseapp.components.contentproviders

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class SyncProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return false
    }

    @Nullable
    override fun query(
        @NonNull uri: Uri, @Nullable projection: Array<String>?, @Nullable selection: String?, @Nullable selectionArgs: Array<String>?, @Nullable sortOrder: String?
    ): Cursor? {
        return null
    }

    @Nullable
    override fun getType(@NonNull uri: Uri): String? {
        return null
    }

    @Nullable
    override fun insert(@NonNull uri: Uri, @Nullable values: ContentValues?): Uri? {
        return null
    }

    override fun delete(@NonNull uri: Uri, @Nullable selection: String?, @Nullable selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        @NonNull uri: Uri, @Nullable values: ContentValues?, @Nullable selection: String?, @Nullable selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}
