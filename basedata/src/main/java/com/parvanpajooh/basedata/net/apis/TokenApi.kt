package com.parvanpajooh.basedata.net.apis

import android.util.Base64
import com.parvanpajooh.basedata.net.models.Token
import kotlinx.coroutines.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

open interface TokenApi {
	private val tokenAuthorization: String
		get() = ("Basic "+Base64.encodeToString("android:androidclientsecret".toByteArray(), Base64.DEFAULT)).trim { it <= ' ' }

	@FormUrlEncoded
	@POST("/uaa/oauth/token")
	@Headers("Authorization: Basic YW5kcm9pZDphbmRyb2lkY2xpZW50c2VjcmV0")
	fun getTokenWithAccountAsync(
			@Field("username") username: String, @Field("password") password: String, @Field("grant_type") grantType: String = "password"): Deferred<Token>

	@FormUrlEncoded
	@POST("/uaa/oauth/token")
	@Headers("Authorization: Basic YW5kcm9pZDphbmRyb2lkY2xpZW50c2VjcmV0")
	fun getTokenWithRefreshTokenAsync(
			@Field("refresh_token") refreshToken: String, @Field("grant_type") grantType: String = "refresh_token"): Deferred<Token>

}
