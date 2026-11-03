package com.example.cskh.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.Dns
import okhttp3.OkHttpClient
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * Custom DNS: Khi hệ thống DNS (Private DNS trên Android / 4G) gặp lỗi phân giải (UnknownHostException)
 * đối với tên miền toctienltd.vn, tự động fallback về IP máy chủ đã cấu hình.
 */
private val fallbackDns = object : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return try {
            Dns.SYSTEM.lookup(hostname)
        } catch (e: UnknownHostException) {
            when (hostname.lowercase()) {
                "beta.toctienltd.vn", "pay.toctienltd.vn", "toctienltd.vn" -> {
                    listOf(InetAddress.getByName("125.253.121.171"))
                }
                else -> throw e
            }
        }
    }
}

actual fun createHttpClientEngine(): HttpClientEngine {
    val defaultVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
    val okHttpClient = OkHttpClient.Builder()
        .dns(fallbackDns)
        .hostnameVerifier { hostname, session ->
            if (hostname.equals("beta.toctienltd.vn", ignoreCase = true) ||
                hostname.equals("pay.toctienltd.vn", ignoreCase = true)
            ) {
                defaultVerifier.verify(hostname, session) || defaultVerifier.verify("toctienltd.vn", session)
            } else {
                defaultVerifier.verify(hostname, session)
            }
        }
        .retryOnConnectionFailure(true)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    return OkHttp.create {
        preconfigured = okHttpClient
    }
}

actual fun createTrustAllEngine(): HttpClientEngine {
    val trustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }
    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf(trustManager), SecureRandom())
    }
    val okHttpClient = OkHttpClient.Builder()
        .dns(fallbackDns)
        .retryOnConnectionFailure(true)
        .sslSocketFactory(sslContext.socketFactory, trustManager)
        .hostnameVerifier { _, _ -> true }
        .build()
    return OkHttp.create {
        preconfigured = okHttpClient
    }
}

