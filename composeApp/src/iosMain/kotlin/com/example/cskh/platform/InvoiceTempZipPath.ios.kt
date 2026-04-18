package com.example.cskh.platform

import platform.Foundation.NSTemporaryDirectory

actual fun invoiceTempZipAbsolutePath(invoiceId: Long): String {
    val dir = NSTemporaryDirectory().trimEnd('/')
    return "$dir/einv_$invoiceId.zip"
}
