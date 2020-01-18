package com.parvanpajooh.basedomain.models.response

data class TokenRes(var accessToken: String,
                    var tokenType: String,
                    var refreshToken: String,
                    var expiresIn: Long)
