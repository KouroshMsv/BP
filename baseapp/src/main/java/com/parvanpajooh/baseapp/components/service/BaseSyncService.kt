package com.parvanpajooh.baseapp.components.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.parvanpajooh.basedata.sync.BaseSyncAdapter
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class BaseSyncService : Service() {

    private var syncAdapter: BaseSyncAdapter? = null
    private val mutex = Mutex()
    override fun onCreate() {
        runBlocking {
            mutex.withLock {
                if (syncAdapter == null) {
                    syncAdapter = getSyncAdapterInstance()
                }
            }
        }
    }

    abstract fun getSyncAdapterInstance(): BaseSyncAdapter


    override fun onBind(intent: Intent): IBinder? {
        return syncAdapter!!.syncAdapterBinder
    }
}