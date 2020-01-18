package com.parvanpajooh.baseapp.components.broadcasts

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.baseapp.Helpers


class TimeChecker : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        PrefHelper.put(BasePrefKey.IS_AUTO_TIME_ZONE.name, Helpers.isTimeAutomatic(context!!))
    }


}
