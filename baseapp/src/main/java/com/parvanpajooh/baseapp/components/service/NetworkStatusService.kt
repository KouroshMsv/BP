package com.parvanpajooh.baseapp.components.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import com.parvanpajooh.baseapp.enums.NetworkStatus
import com.parvanpajooh.baseapp.utils.isOnline
import com.parvanpajooh.baseapp.models.eventbus.NetworkEvent
import dev.kourosh.baseapp.onMain
import dev.kourosh.basedomain.launchIO
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


class NetworkStatusService : Service() {
    companion object {
        var running = false
    }

    var disposable: Disposable? = null
    override fun onCreate() {
        super.onCreate()
        running = true
        disposable = Observable.create<NetworkStatus> { st ->
            Observable.interval(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    launchIO {
                        val status = isOnline().await()
                        onMain {
                            st.onNext(status)
                        }
                    }

                }, {
                    st.onNext(NetworkStatus.InternetIsDisconnected)
                })
        }.distinctUntilChanged().subscribe { status ->
            if (status != null)
                EventBus.getDefault().post(
                    NetworkEvent(
                        status == NetworkStatus.Connected,
                        status.message
                    )
                )
        }
    }


    override fun onBind(intent: Intent?) = Binder()

    override fun onDestroy() {
        running = false

        super.onDestroy()
    }
}