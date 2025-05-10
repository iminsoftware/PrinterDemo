package com.imin.newprinter.demo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener
import javax.jmdns.impl.DNSCache
import javax.jmdns.impl.JmDNSImpl
import javax.jmdns.impl.tasks.resolver.ServiceResolver

class NetworkServiceHelper(context: Context) : ServiceListener {
    private val TAG = "PrintDemo_WifiFragment  JmDNS"
    private val jmdns: JmDNS
    private val multicastLock: WifiManager.MulticastLock

    init {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifiManager.createMulticastLock("jmdns_lock").apply {
            setReferenceCounted(false)
            acquire()
        }


//
//            // 配置Socket参数（3.5.8+ 公开API）
//            socket.apply {
//                soTimeout = 1500       // 接收超时1.5秒
//                broadcast = true       // 启用广播
//                reuseAddress = true    // 地址复用
//            }
//        }
        val ipAddress = getLocalIpAddress(context)
        Log.d(TAG ,"Service ipAddress: $ipAddress")
        // 在 init 块中修改 JmDNS 创建方式
       // jmdns = JmDNS.create(ipAddress, "AndroidDevice")
        jmdns = JmDNS.create(ipAddress, "AndroidDevice")


    }
    // 核心优化方法
//    private fun createOptimizedJmDNS(address: InetAddress): JmDNS {
//        return JmDNS.create(address, "AndroidDevice").apply {
//            // 类型安全转换（JmDNSImpl是公开类）
//            if (this is JmDNSImpl) {
//                // === 性能关键参数 ===
//                queryTimeout = 1200        // 查询超时1.2秒（默认5秒）
//                recurrenceInterval = 300   // 服务刷新间隔300ms（默认1秒）
//                responseDelay = 100        // 响应延迟100ms（默认500ms）
//
//                // === 缓存策略 ===
//                cache = DNSCache().apply {
//                    setMaxCacheSize(200)   // 最大缓存条目数（默认100）
//                    setMaxTTL(30)          // 缓存有效期30秒（默认300）
//                }
//
//                // === 并行解析器配置 ===
//                serviceResolver = object : ServiceResolver(this) {
//                    // 必须重写的方法
//                    override fun isParallel() = true
//                    override fun getMaxConcurrentRequests() = 6
//                    override fun getRetryInterval() = 400L
//                }
//            }
//
//            // === 网络层优化 ===
//            socket.apply {
//                soTimeout = 1500       // Socket接收超时
//                broadcast = true       // 启用广播
//                reuseAddress = true    // 地址复用
//            }
//        }
//    }

    // 注册服务监听（替代 registerServiceType）
    fun startDiscovery(vararg serviceTypes: String) {
        serviceTypes.forEach { type ->
            jmdns.addServiceListener(type, this)
            // 主动发送探测请求
            CoroutineScope(Dispatchers.IO).launch {
                repeat(3) {  // 发送3次快速探测
                    jmdns.list(type)  // 主动查询
                    delay(500)
                }
            }
        }
    }

    // 实现 ServiceListener 接口
    override fun serviceAdded(event: ServiceEvent) {
        Log.d(TAG ,"Service serviceAdded : ${event.name}")
        event.dns.requestServiceInfo(event.type, event.name, true)
    }

    override fun serviceRemoved(event: ServiceEvent) {
        Log.d(TAG ,"Service removed: ${event.name}")
    }

    override fun serviceResolved(event: ServiceEvent) {
        val info = event.info
        //1B010000C0A88362CD313233000000000000000000000000000000B1
       // 1B010003C0A88362CA313233000000000000000000000000000000B1
        Log.d(TAG ,"Resolved: ${info.name} (${info.hostAddress}:${info.port})" +
                " textBytes= ${BytesUtils.bytesToHex(info.textBytes)} , textString= ${info.textString}  ,  ${info.protocol}")
    }

//    private fun getLocalIpAddress(wifiManager: WifiManager): InetAddress {
//        val ipInt = wifiManager.connectionInfo.ipAddress
//        return InetAddress.getByAddress(byteArrayOf(
//            (ipInt and 0xFF).toByte(),
//            (ipInt shr 8 and 0xFF).toByte(),
//            (ipInt shr 16 and 0xFF).toByte(),
//            (ipInt shr 24 and 0xFF).toByte()
//        ))
//    }

    private fun getLocalIpAddress(context: Context): InetAddress {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val linkProperties = connectivityManager.getLinkProperties(activeNetwork)

        // 优先选择 IPv4 地址
        linkProperties?.linkAddresses?.forEach { addr ->
            if (addr.address is Inet4Address && !addr.address.isLoopbackAddress) {
                return addr.address
            }
        }

        // 备选方案：传统方式获取
        return try {
            val enumInterfaces = NetworkInterface.getNetworkInterfaces()
            while (enumInterfaces.hasMoreElements()) {
                val intf = enumInterfaces.nextElement()
                if (intf.isUp && !intf.isLoopback) {
                    val enumAddr = intf.inetAddresses
                    while (enumAddr.hasMoreElements()) {
                        val addr = enumAddr.nextElement()
                        if (addr is Inet4Address) {
                            return addr
                        }
                    }
                }
            }
            throw IllegalStateException("No valid network interface found")
        } catch (e: Exception) {
            throw RuntimeException("Network interface error", e)
        }
    }

    fun cleanup() {
        jmdns.close()
        multicastLock.release()
    }
}