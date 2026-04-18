package com.example.cskh.platform

/** Absolute path for a temporary zip file used only while extracting `.html` from the archive. */
expect fun invoiceTempZipAbsolutePath(invoiceId: Long): String
