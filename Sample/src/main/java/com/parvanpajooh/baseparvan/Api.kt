package com.parvanpajooh.baseparvan

import com.parvanpajooh.basedata.BaseDataModuleRepositoryImpl
import com.parvanpajooh.basedata.net.BaseRestApiImpl
import dev.kourosh.basedomain.Result

class Api() : BaseDataModuleRepositoryImpl(BaseRestApiImpl("https://172.24.24.38")) {
    override suspend fun initialize(token: String): Result<Unit> {
        return Result.Success(Unit)

    }
}