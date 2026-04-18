package com.example.cskh.platform

import android.content.Context

internal object AndroidApplicationHolder {
    lateinit var application: Context
        private set

    fun init(context: Context) {
        application = context.applicationContext
    }
}
