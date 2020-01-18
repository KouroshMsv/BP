package com.parvanpajooh.basedata.net

import android.annotation.SuppressLint
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.parvanpajooh.basedata.BuildConfig
import dev.kourosh.basedomain.classOf
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal open class BaseApiService(
    private val url: String,
    isHttps: Boolean,
    private val connectTimeout: Long = 10, private val readWriteTimeout: Long = 60
) {

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    protected val okHttpClientBuilder: OkHttpClient.Builder
        get() {
            val builder = OkHttpClient.Builder()
            if (BuildConfig.DEBUG) {
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
        return retrofit.create(classOf<T>())
    }

}