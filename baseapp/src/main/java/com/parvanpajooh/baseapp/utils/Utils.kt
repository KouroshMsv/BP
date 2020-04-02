package com.parvanpajooh.baseapp.utils

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresPermission

fun Context.callFromDialer(number: String) {
    try {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$number")
        startActivity(callIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(this, "No SIM Found", Toast.LENGTH_LONG).show()
    }
}

@RequiresPermission(Manifest.permission.CALL_PHONE)
fun Context.callDirect(number: String) {
    try {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$number")
        startActivity(callIntent)
    } catch (e: SecurityException) {
        Toast.makeText(this, "Need call permission", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(this, "No SIM Found", Toast.LENGTH_LONG).show()
    }
}

fun rotate(
    view: View,
    from: Float,
    to: Float,
    duration: Long = 200,
    repeatCount: Int = 0
) {
    val rotate = ObjectAnimator.ofFloat(view, "rotation", from, to)
    rotate.repeatCount = repeatCount
    rotate.duration = duration
    rotate.start()
}