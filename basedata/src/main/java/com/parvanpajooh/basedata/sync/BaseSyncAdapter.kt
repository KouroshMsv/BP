package com.parvanpajooh.basedata.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import com.parvanpajooh.basedomain.interactor.factory.UseCaseFactory
import dev.kourosh.basedomain.launchIO
import dev.kourosh.basedomain.logE

open class BaseSyncAdapter(context: Context, autoInitialize: Boolean, private val uc: UseCaseFactory) : AbstractThreadedSyncAdapter(context, autoInitialize) {
    override fun onPerformSync(account: Account, extras: Bundle, authority: String, provider: ContentProviderClient, syncResult: SyncResult) {
        logE("onPerformSync for account[${account.name}]")

    }

}
