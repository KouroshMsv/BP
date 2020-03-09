package com.parvanpajooh.baseparvan

import com.parvanpajooh.baseapp.infrastructure.BaseApp

class App:BaseApp() {
    override val authority: String
        get() = ""
    override val defaultFontPath: String
        get() = "is"
}