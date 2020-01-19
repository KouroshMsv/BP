package com.parvanpajooh.baseapp.ui.init

import com.parvanpajooh.baseapp.infrastructure.mvvm.activity.BaseActivityViewModel
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.baseapp.SingleLiveEvent

class InitActivityVM : BaseActivityViewModel() {
    val loggedIn = SingleLiveEvent<Boolean>()
    fun autoLogin() {
        loggedIn.value = PrefHelper.get(
            BasePrefKey.INITIALIZED.name,
            false
        ) && PrefHelper.get<String?>(BasePrefKey.USERNAME.name) != null
    }

}