package com.parvanpajooh.baseapp.components.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.parvanpajooh.basedata.sync.BaseSyncAdapter

abstract class BaseSyncService : Service() {

    private val sSyncAdapterLock = Any()
    private var syncAdapter: BaseSyncAdapter? = null

    override fun onCreate() {
        synchronized(sSyncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = syncAdapterInstance
            }
        }
    }

    abstract val syncAdapterInstance: BaseSyncAdapter


    override fun onBind(intent: Intent): IBinder? {
        return syncAdapter!!.syncAdapterBinder
    }
}