package com.example.cskh.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURLAuthenticationMethodServerTrust
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSessionAuthChallengePerformDefaultHandling
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.credentialForTrust
import platform.Foundation.serverTrust

@OptIn(ExperimentalForeignApi::class)
actual fun createHttpClientEngine(): HttpClientEngine = Darwin.create {
    handleChallenge { _, _, challenge, completionHandler ->
        val protectionSpace = challenge.protectionSpace
        val host = protectionSpace.host.lowercase()
        if (protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust &&
            (host == "toctienltd.vn" || host.endsWith(".toctienltd.vn"))
        ) {
            val trust = protectionSpace.serverTrust
            if (trust != null) {
                completionHandler(
                    NSURLSessionAuthChallengeUseCredential,
                    NSURLCredential.credentialForTrust(trust),
                )
                return@handleChallenge
            }
        }
        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun createTrustAllEngine(): HttpClientEngine = Darwin.create {
    handleChallenge { _, _, challenge, completionHandler ->
        val protectionSpace = challenge.protectionSpace
        if (protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust) {
            val trust = protectionSpace.serverTrust
            if (trust != null) {
                completionHandler(
                    NSURLSessionAuthChallengeUseCredential,
                    NSURLCredential.credentialForTrust(trust),
                )
                return@handleChallenge
            }
        }
        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
    }
}
