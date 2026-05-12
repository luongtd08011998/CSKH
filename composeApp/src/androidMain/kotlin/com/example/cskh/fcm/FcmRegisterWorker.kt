package com.example.cskh.fcm

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cskh.platform.FcmDeviceSync
import org.koin.core.context.GlobalContext

class FcmRegisterWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "WorkManager: attempting FCM register")
        return try {
            val sync = GlobalContext.get().get<FcmDeviceSync>()
            sync.registerIfLoggedIn()
            Log.d(TAG, "WorkManager: FCM register success")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "WorkManager: FCM register failed: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "FCM"
        const val WORK_NAME = "fcm_register_fallback"
    }
}
