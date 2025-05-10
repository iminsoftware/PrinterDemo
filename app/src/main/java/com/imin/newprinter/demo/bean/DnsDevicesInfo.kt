package com.imin.newprinter.demo.bean

/**
 * @Author: hy
 * @date: 2025/5/10
 * @description:
 */
data class DnsDevicesInfo(
    val serviceName: String,
    val serviceType: String,
    val hostAddress: String,
    val port: Int,
    val txtRecords: Map<String, String>,
    val hostName: String = ""
)