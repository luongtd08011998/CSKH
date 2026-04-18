package com.example.cskh.platform

/**
 * Base URL for an HTTP API running on the development machine, as seen from a simulator.
 *
 * - **Android Emulator**: the host loopback is reachable at `10.0.2.2` (not `localhost`).
 * - **iOS Simulator**: shares the Mac network stack; `127.0.0.1` reaches services bound on the Mac.
 *
 * On a physical device, use your machine's LAN IP instead (this value will not be correct).
 */
expect fun defaultDevMachineApiBaseUrl(port: Int = 8080): String
