package com.parvanpajooh.baseapp.components.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import dev.kourosh.basedomain.logE


class SmsBroadcastReceiver : BroadcastReceiver() {
    private val serviceProviderNumber = "+98500048059"
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsSender = ""
            var smsBody = ""
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsSender = smsMessage.displayOriginatingAddress
                smsBody += smsMessage.messageBody
            }
            if (smsSender == serviceProviderNumber) {
                logE(smsSender)
                logE(smsBody)
            }
        }
    }
}