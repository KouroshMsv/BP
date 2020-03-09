package com.parvanpajooh.baseparvan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.hawk.Hawk
import com.parvanpajooh.baseapp.components.service.NetworkStatusService
import com.parvanpajooh.baseapp.models.eventbus.NetworkEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Hawk.init(applicationContext).build()
        setContentView(R.layout.activity_main)
        startService(Intent(applicationContext, NetworkStatusService::class.java))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChange(data: NetworkEvent) {
        Toast.makeText(applicationContext, data.message, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}