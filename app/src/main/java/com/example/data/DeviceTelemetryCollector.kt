package com.example.data

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build

data class DeviceTelemetry(
    val manufacturer: String,
    val model: String,
    val brand: String,
    val hardware: String,
    val deviceCode: String,
    val androidVersion: String,
    val sdkInt: Int,
    val securityPatch: String,
    val screenResolution: String,
    val screenDensityDpi: Int,
    val batteryPercentage: Int,
    val isCharging: Boolean,
    val networkType: String,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val isLowMemory: Boolean
)

object DeviceTelemetryCollector {
    fun collect(context: Context): DeviceTelemetry {
        // Battery
        val batteryStatus: Intent? = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level != -1 && scale != -1) (level * 100 / scale.toFloat()).toInt() else 85
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        // Memory
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val availRamMb = memInfo.availMem / (1024 * 1024)

        // Network
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connManager.activeNetwork
        val caps = connManager.getNetworkCapabilities(activeNetwork)
        val networkType = when {
            caps == null -> "Offline"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi High-Band"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular 5G/LTE"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Connected (Other)"
        }

        // Display
        val displayMetrics = context.resources.displayMetrics
        val screenRes = "${displayMetrics.widthPixels} x ${displayMetrics.heightPixels}"
        val densityDpi = displayMetrics.densityDpi

        return DeviceTelemetry(
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            brand = Build.BRAND.replaceFirstChar { it.uppercase() },
            hardware = Build.HARDWARE,
            deviceCode = Build.DEVICE,
            androidVersion = Build.VERSION.RELEASE,
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "N/A",
            screenResolution = screenRes,
            screenDensityDpi = densityDpi,
            batteryPercentage = batteryPct,
            isCharging = isCharging,
            networkType = networkType,
            totalRamMb = totalRamMb,
            availableRamMb = availRamMb,
            isLowMemory = memInfo.lowMemory
        )
    }
}
