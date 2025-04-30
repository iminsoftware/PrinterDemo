package com.imin.newprinter.demo.bean

/**
 * @Author: hy
 * @date: 2025/4/28
 * @description:
 */
data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val rssiBle: Int,
    val rssiWifi: Int,
    val wifiConnectStatus: Int,
    val printerWorkingStatus: Int,
    val ioStatus: String,
    val ipAddress: String,
    val wifiName: String
)
