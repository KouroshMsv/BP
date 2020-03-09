package com.parvanpajooh.baseapp.components.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import com.parvanpajooh.baseapp.enums.NetworkStatus
import com.parvanpajooh.baseapp.models.eventbus.NetworkEvent
import com.parvanpajooh.baseapp.utils.isOnline
import dev.kourosh.baseapp.onMain
import dev.kourosh.basedomain.launchIO
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


class NetworkStatusService : Service() {
    companion object {
        var running = false
    }
    override fun onCreate() {
        super.onCreate()
        running = true
        flow {
            launchIO {
                while (running) {
                    delay(4000)
                    onMain {
                        emit(isOnline().await())
                    }
                }
            }
        }.distinctUntilChanged { old, new ->
            EventBus.getDefault().post(
                NetworkEvent(
                    new == NetworkStatus.Connected,
                    new.message
                )
            )
            old != new
        }
    }


    override fun onBind(intent: Intent?) = Binder()

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }
}