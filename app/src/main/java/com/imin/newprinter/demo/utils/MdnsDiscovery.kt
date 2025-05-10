package com.imin.newprinter.demo.utils
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import com.imin.newprinter.demo.bean.DnsDevicesInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlin.coroutines.CoroutineContext

/**
 * @Author: hy
 * @date: 2025/5/10
 * @description:
 */
@SuppressLint("StaticFieldLeak")
object MdnsDiscovery {
    private lateinit var appContext: Context
    private val nsdManager by lazy { appContext.getSystemService(Context.NSD_SERVICE) as NsdManager }

    // 使用同步锁保证线程安全
    private val syncLock = Any()
    private var discoveryJob: Job? = null
    private val resolveJobs = mutableMapOf<String, Job>()

    // 添加 SupervisorJob 防止协程异常传播
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        CoroutineScope(Dispatchers.Main).launch {
            handleError(-100, "Coroutine Error: ${e.message?.take(200) ?: "Unknown"}")
        }
    }

    private val coroutineScope = CoroutineScope(
        Dispatchers.Main.immediate
                + SupervisorJob()
                + exceptionHandler
    )

    // 增加背压策略
    private val _discoveryEvents = MutableSharedFlow<DiscoveryEvent>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val discoveryEvents = _discoveryEvents.asSharedFlow()

    fun init(context: Context) {
        if (!::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }

    // 添加销毁方法
    fun destroy() {
        this.coroutineScope.cancel()
        stopDiscovery()
    }

    sealed class DiscoveryEvent {
        data class ServiceFound(val device: DnsDevicesInfo) : DiscoveryEvent() // 改为返回DeviceInfo
        data class ServiceLost(val serviceName: String) : DiscoveryEvent()
        object DiscoveryStarted : DiscoveryEvent()
        object DiscoveryStopped : DiscoveryEvent()
        data class Error(val code: Int, val message: String) : DiscoveryEvent()
    }
    // 增加网络状态检查
    private fun checkNetwork(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }

    fun startDiscovery(serviceType: String) {
        synchronized(syncLock) {
            checkInitialized()
            discoveryJob = coroutineScope.launch {
            if (!checkNetwork()) {
                Log.d("PrintDemo_WifiFragment", "网络未连接，取消发现")

                handleError(-201, "Network not connected")
                return@launch
            }

            // 增加服务类型格式验证
                // 修改后（正确）
                var serviceTypeRegex = Regex("^_[a-zA-Z\\d-]+\\._(tcp|udp)\\.$")
                if (!serviceType.matches(serviceTypeRegex)) {
                    Log.e("PrintDemo_WifiFragment", "非法服务类型格式: $serviceType")
                    handleError(-202, "Invalid service type format")
                    return@launch
                }

            Log.d("PrintDemo_WifiFragment", "startDiscovery with type: $serviceType")
            stopDiscovery()


                val listener = createDiscoveryListener()
                withContext(Dispatchers.IO) {
                    try {
                        Log.d("PrintDemo_WifiFragment", "开始NSD发现流程")
                        nsdManager.discoverServices(
                            serviceType,
                            NsdManager.PROTOCOL_DNS_SD,
                            listener
                        )
                    } catch (e: SecurityException) {
                        Log.e("PrintDemo_WifiFragment", "权限异常: ${e.message}")
                        handleError(-203, "Permission denied")
                    } catch (e: Exception) {
                        Log.e("PrintDemo_WifiFragment", "发现异常: ${e.stackTraceToString()}")
                        handleError(-204, "Discovery failed")
                    }
                }
            }
        }
    }

    fun stopDiscovery() {
        synchronized(syncLock) {
            discoveryJob?.cancel()
            resolveJobs.values.forEach { it.cancel() }
            resolveJobs.clear()
        }
    }

    private fun createDiscoveryListener() = object : NsdManager.DiscoveryListener {
        override fun onStartDiscoveryFailed(type: String?, errorCode: Int) {
            Log.d("PrintDemo_WifiFragment",
                "onStartDiscoveryFailed:type=  ${type} errorCode=$errorCode")
            launchSafely {
                handleError(errorCode, "Discovery start failed")
                stopDiscovery()
            }
        }

        override fun onStopDiscoveryFailed(type: String?, errorCode: Int) {
            Log.d("PrintDemo_WifiFragment",
                "onStopDiscoveryFailed:type  ${type} errorCode=$errorCode")
            launchSafely {
                handleError(errorCode, "Discovery stop failed")
            }
        }

        override fun onDiscoveryStarted(type: String?) {
            Log.d("PrintDemo_WifiFragment",
                "onDiscoveryStarted:type  ${type}")
            launchSafely {
                emitSafely(DiscoveryEvent.DiscoveryStarted)
            }
        }

        override fun onDiscoveryStopped(type: String?) {
            Log.d("PrintDemo_WifiFragment",
                "onDiscoveryStopped:type  ${type}")
            launchSafely {
                emitSafely(DiscoveryEvent.DiscoveryStopped)
            }
        }

        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            Log.d("PrintDemo_WifiFragment",
                "onServiceFound: ${serviceInfo.serviceName}")
            resolveService(serviceInfo)
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            Log.d("PrintDemo_WifiFragment",
                "onServiceLost: ${serviceInfo.serviceName}")
            launchSafely {
                _discoveryEvents.tryEmit(DiscoveryEvent.ServiceLost(serviceInfo.serviceName))
                cancelResolveJob(serviceInfo.serviceName)
            }
        }
    }

    private fun resolveService(serviceInfo: NsdServiceInfo) {
        Log.d("PrintDemo_WifiFragment", "开始解析服务: ${serviceInfo.serviceName}")
        val job = coroutineScope.launch(Dispatchers.IO) {
            try {
                nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(info: NsdServiceInfo, errorCode: Int) {
                        Log.w("PrintDemo_WifiFragment",
                            "解析失败: ${info.serviceName}, code=$errorCode")
                        // 增加重试逻辑
                        if (errorCode == NsdManager.FAILURE_ALREADY_ACTIVE) {
                            Log.d("PrintDemo_WifiFragment", "解析冲突，重新尝试...")
                            resolveService(info)
                        } else {
                            launchSafely(Dispatchers.Main) {
                                handleError(errorCode, "Resolve failed: ${info.serviceName}")
                            }

                        }
                    }

                    override fun onServiceResolved(info: NsdServiceInfo) {
                        Log.d("PrintDemo_WifiFragment",
                            "解析成功: ${info.serviceName} -> ${info.host?.hostAddress}")
                        launchSafely(Dispatchers.Main) {
                            val device = parseServiceInfo(info)
                            Log.d("PrintDemo_WifiFragment",
                                "转换设备信息: ${device.serviceName} [${device.hostAddress}]")
                            _discoveryEvents.tryEmit(DiscoveryEvent.ServiceFound(device))
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("PrintDemo_WifiFragment", "解析异常: ${e.stackTraceToString()}")
                handleError(-1, "Resolve error: ${e.message}")
            }
        }
        resolveJobs[serviceInfo.serviceName] = job
    }

    // 增强的主机地址解析
    private fun parseHostAddress(host: java.net.InetAddress?): String {
        return try {
            host?.hostAddress?.takeIf { addr ->
                addr.matches(Regex("\\d+\\.\\d+\\.\\d+\\.\\d+"))
            } ?: run {
                Log.w("PrintDemo_WifiFragment", "无效IP地址: ${host?.hostAddress}")
                "0.0.0.0"
            }
        } catch (e: Exception) {
            Log.e("PrintDemo_WifiFragment", "解析IP异常: ${e.message}")
            "0.0.0.0"
        }
    }

    // 增强的解析方法
    private fun parseServiceInfo(info: NsdServiceInfo): DnsDevicesInfo {
        return try {
            val host = info.host?.apply {
                // 增强Android 8.0+兼容性处理
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        val method = javaClass.getMethod("setHostname", String::class.java)
                        method.invoke(this, hostAddress)
                    } catch (e: Exception) {
                        Log.w(
                            "PrintDemo_WifiFragment",
                            "设置hostname失败: ${e.javaClass.simpleName}"
                        )
                    }
                }
            }

            DnsDevicesInfo(
                serviceName = info.serviceName,
                serviceType = info.serviceType,
                hostAddress = parseHostAddress(host),
                port = info.port,
                txtRecords = parseTxtRecords(info.attributes),
                hostName = host?.hostName?.takeIf { it.isNotEmpty() } ?: ""
            )
        } catch (e: Exception) {
           // Log.e("PrintDemo_WifiFragment", "解析服务信息异常: ${e.stackTraceToString()}")
            DnsDevicesInfo(
                serviceName = "Unknown error",
                serviceType = info.serviceType,
                hostAddress =" parseHostAddress(host)",
                port = info.port,
                txtRecords = parseTxtRecords(info.attributes),
                hostName = ""
            )
        }
    }



    // 解析TXT记录
    private fun parseTxtRecords(attributes: Map<String, ByteArray>?): Map<String, String> {
        return attributes?.mapValues { entry ->
            try {
                String(entry.value, Charsets.UTF_8)
            } catch (e: Exception) {
                "N/A"
            }
        } ?: emptyMap()
    }

    private fun cancelResolveJob(serviceName: String) {
        resolveJobs[serviceName]?.cancel()
        resolveJobs.remove(serviceName)
    }

    // 安全的协程启动方法
    // 增强的launch方法
    private fun launchSafely(
        context: CoroutineContext = Dispatchers.Main,
        block: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope.launch(context) {
            try {
                block()
            } catch (e: CancellationException) {
                // 正常取消不处理
            } catch (e: Exception) {
                handleError(-101, "Launch error: ${e.message}")
            }
        }
    }

    // 安全的事件发送方法
    private suspend fun emitSafely(event: DiscoveryEvent) {
        try {
            _discoveryEvents.emit(event)
        } catch (e: Exception) {
            handleError(-5, "Event emit failed: ${e.message}")
        }
    }

    private suspend fun handleError(code: Int, message: String) {
        emitSafely(DiscoveryEvent.Error(code, message))
    }

    private fun checkInitialized() {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("MdnsDiscovery must be initialized with context first. " +
                    "Call MdnsDiscovery.init(context) in Application.onCreate()")
        }
    }
}