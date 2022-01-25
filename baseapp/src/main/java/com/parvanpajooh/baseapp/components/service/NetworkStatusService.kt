package com.parvanpajooh.baseapp.components.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import com.parvanpajooh.baseapp.enums.NetworkStatus
import com.parvanpajooh.baseapp.models.eventbus.NetworkEvent
import com.parvanpajooh.baseapp.utils.isOnline
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.Executors


class NetworkStatusService : Service() {
    private val coroutineScope by lazy {
        CoroutineScope(Executors.newFixedThreadPool(2).asCoroutineDispatcher())
    }
    companion object {
        var running = false
    }

    override fun onCreate() {
        super.onCreate()
        running = true
        val flow = channelFlow {
            withContext(Dispatchers.IO) {
                while (running) {
                    send(isOnline())
                    delay(5000)
                }
            }
        }.distinctUntilChanged { old, new -> new.ordinal == old.ordinal }

        coroutineScope.launch(Dispatchers.Main) {
            flow.collect { value ->
                EventBus.getDefault()
                    .post(
                        NetworkEvent(value == NetworkStatus.Connected, value.message)
                    )
            }
        }
    }


    override fun onBind(intent: Intent?) = Binder()

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }
}