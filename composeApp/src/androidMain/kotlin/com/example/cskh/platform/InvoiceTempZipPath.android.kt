package com.example.cskh.platform

import java.io.File

actual fun invoiceTempZipAbsolutePath(invoiceId: Long): String =
    File(AndroidApplicationHolder.application.cacheDir, "einv_$invoiceId.zip").absolutePath
