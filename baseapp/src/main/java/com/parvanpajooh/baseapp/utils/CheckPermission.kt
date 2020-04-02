package com.parvanpajooh.baseapp.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Activity.checkPermission(request: PermissionRequest): PermissionResponse {
    return if (ContextCompat.checkSelfPermission(
            this,
            request.permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {

        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, request.permission)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            PermissionResponse.DENIED
        } else {
            // No explanation needed, we can request the permission.
//            requestPermission(request)
            PermissionResponse.FIRST_DENIED
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    } else {
        PermissionResponse.GRANTED
    }
}

fun Activity.requestPermission(request: PermissionRequest) {
    ActivityCompat.requestPermissions(this, arrayOf(request.permission), request.requestCode)
}

fun Activity.requestPermission(request: List<PermissionRequest>, requestCode: Int) {
    ActivityCompat.requestPermissions(
        this,
        request.map { it.permission }.toTypedArray(),
        requestCode
    )
}

enum class PermissionRequest(val permission: String, val requestCode: Int, val message: String) {
    ACCESS_FINE_LOCATION(
        Manifest.permission.ACCESS_FINE_LOCATION,
        101,
        "برای ثبت منطقه، برنامه نیاز به دسترسی به لوکیشن دارد."
    ),
    CAMERA(
        Manifest.permission.CAMERA,
        102,
        "برای اسکن بارکد، برنامه نیاز به دسترسی به دوربین دارد."
    ),
    CALL_PHONE(
        Manifest.permission.CALL_PHONE,
        103,
        "برای تماس مستقیم با مشتریان، برنامه نیاز به دسترسی به تماس ها دارد."
    ),
    WRITE_EXTERNAL_STORAGE(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        104,
        "برای ذخیره فایل ها، برنامه نیاز به دسترسی به فایل ها دارد."
    ),
}

enum class PermissionResponse {
    GRANTED, FIRST_DENIED, DENIED
}