package com.parvanpajooh.basedata.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import com.parvanpajooh.basedomain.interactor.factory.BaseUseCaseI
import dev.kourosh.basedomain.logI

open class BaseSyncAdapter(context: Context, autoInitialize: Boolean, private val uc: BaseUseCaseI) : AbstractThreadedSyncAdapter(context, autoInitialize) {
    override fun onPerformSync(account: Account, extras: Bundle, authority: String, provider: ContentProviderClient, syncResult: SyncResult) {
        logI("onPerformSync for account[${account.name}] ,\nextras: [$extras],\nauthority: [$authority],\nprovider: [$provider]\n syncResult: [$syncResult]")
    }

}
