package com.parvanpajooh.basedata.net

import android.annotation.SuppressLint
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

open class BaseApiService(
    private val url: String,

    private val debuggable: Boolean,
    isHttps: Boolean,
    private val connectTimeout: Long = 5, private val readWriteTimeout: Long = 60,
    private val jsonConfiguration: JsonConfiguration = JsonConfiguration(ignoreUnknownKeys = true)
) {
    private val contentType = "application/json".toMediaType()
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(
                Json(jsonConfiguration)
                    .asConverterFactory(contentType)
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    protected val okHttpClientBuilder: OkHttpClient.Builder
        get() {
            val builder = OkHttpClient.Builder()
            if (debuggable) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                builder.addInterceptor(httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                })
            }
            return builder
                .writeTimeout(readWriteTimeout, TimeUnit.SECONDS)
                .readTimeout(readWriteTimeout, TimeUnit.SECONDS)
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
        }
    protected open val client: OkHttpClient =
        if (isHttps) httpsClient else okHttpClientBuilder.build()

    private val httpsClient: OkHttpClient
        get() {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        chain: Array<java.security.cert.X509Certificate>,
                        authType: String
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        chain: Array<java.security.cert.X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                })

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())

                val sslSocketFactory = sslContext.socketFactory

                val builder = okHttpClientBuilder
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier(HostnameVerifier { _, _ -> true })
                return builder.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

    inline fun <reified T> create(): T {
        return retrofit.create(T::class.java)
    }

}
