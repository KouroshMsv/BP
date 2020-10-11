package com.parvanpajooh.baseparvan

import com.parvanpajooh.baseapp.infrastructure.BaseActivity
import com.parvanpajooh.baseapp.utils.PermissionRequest
import dev.kourosh.basedomain.logE

class MainActivity : BaseActivity(
    R.layout.activity_main, listOf(
        PermissionRequest.ACCESS_FINE_LOCATION,
        PermissionRequest.CALL_PHONE,
        PermissionRequest.CAMERA
    )
) {
    override fun permissionChecked() {
        launchIO {
            Api().getTokenWithAccount("logistic.mehrasa", "1005254").parseOnMain({
                logE(it)
            }, { _, _ -> })
        }
    }

}