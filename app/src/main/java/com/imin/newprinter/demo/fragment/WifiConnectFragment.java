package com.imin.newprinter.demo.fragment;

import static com.imin.newprinter.demo.MainActivity.requestPermissionCode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.feature.tui.util.ToastUtil;
import com.imin.newprinter.demo.MainActivity;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.ListAdapter;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentWifiConnectBinding;
import com.imin.newprinter.demo.utils.ExecutorServiceManager;
import com.imin.newprinter.demo.utils.LoadingDialogUtil;
import com.imin.newprinter.demo.utils.NetworkValidator;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.newprinter.demo.utils.WifiKeyName;
import com.imin.newprinter.demo.utils.WifiScannerSingleton;
import com.imin.newprinter.demo.view.OnSingleClickListener;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @Author: hy
 * @date: 2025/4/18
 * @description:
 */
public class WifiConnectFragment extends BaseFragment implements WifiScannerSingleton.WifiListListener {
    private static final String TAG = "PrintDemo_WifiConnectFragment";
    private com.imin.newprinter.demo.databinding.FragmentWifiConnectBinding binding;
    ArrayList<String> list = new ArrayList<>();


    private static final String ARG_WIFI_LIST = "wifiList";
    private String baseIp = "";//获取当前连接USB底座的IP
    private Future wifiListFuture;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWifiConnectBinding.inflate(inflater);
        wifiScanner = WifiScannerSingleton.getInstance(getContext());
        initView();
        initData();
        return binding.getRoot();
    }

    boolean isOpenEasy = false;
    boolean outoConnect = true;//自动连接   手动输入ip连接

    private ArrayAdapter<String> adapter;

    boolean isVisibleToView = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser + "    " + isResumed());
        isVisibleToView = isVisibleToUser;
        if (isVisibleToUser && isResumed()) {
            // 当 Fragment 对用户可见时执行操作（兼容旧版本）


            initData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + getUserVisibleHint());

    }

    WifiScannerSingleton wifiScanner;

    public WifiScannerSingleton getWifiScanner() {
        return wifiScanner;
    }


    private void startWifiScan() {
        if (wifiScanner != null) {
            wifiScanner.stopWifiScan();
            wifiScanner.startWifiScan(this);
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        Log.d(TAG, "加载页面: " + list.size());
        if (list != null && list.size() > 0) {
            binding.ssidSpinner.setText(list.get(0));
        }

        binding.ssidSpinner.setOnClickListener(view -> {
            if (list != null && list.size() > 0) {
                showPopup(view.getContext(), list, view);
            }
        });


        binding.autoConnectIv.setImageResource(R.drawable.ic_check);
        binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_close);
        // 切换为密码隐藏（星号显示）
        binding.pwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

        binding.ipEt.setEnabled(false);


        binding.connectNetworkTv.setOnClickListener(view -> {

            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green60_corner_5));
            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
            binding.clConnectNetwork.setVisibility(View.VISIBLE);
            binding.clConnectIP.setVisibility(View.INVISIBLE);
        });

        binding.connectTv.setOnClickListener(v -> {

            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green60_corner_5));
            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
            binding.clConnectNetwork.setVisibility(View.INVISIBLE);
            binding.clConnectIP.setVisibility(View.VISIBLE);
            Log.d(TAG, "加载页面: " + binding.clConnectIP.getVisibility());
        });

        binding.autoLy.setOnClickListener(view -> {
            binding.autoConnectIv.setImageResource(R.drawable.ic_check);
            binding.ipIv.setImageResource(R.drawable.ic_uncheck);
            binding.ipEt.setEnabled(false);
            outoConnect = true;
            binding.getConnectIPTv.setEnabled(true);
            binding.getConnectIPTv.setBackground(getResources().getDrawable(R.drawable.dra_blue_corner_5));

        });
        binding.ipLy.setOnClickListener(view -> {
            binding.ipIv.setImageResource(R.drawable.ic_check);
            binding.autoConnectIv.setImageResource(R.drawable.ic_uncheck);
            binding.ipEt.setEnabled(true);
            outoConnect = false;
            binding.getConnectIPTv.setEnabled(false);
            binding.getConnectIPTv.setBackground(getResources().getDrawable(R.drawable.dra_gray_corner_5));
        });

        binding.flPwd.setOnClickListener(view -> {
            isOpenEasy = !isOpenEasy;
            if (isOpenEasy) {
                binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_open);
                // 切换为明文显示
                binding.pwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.pwdEt.setSelection(binding.pwdEt.getText().length());

            } else {
                binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_close);
                // 切换为密码隐藏（星号显示）
                binding.pwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.pwdEt.setSelection(binding.pwdEt.getText().length());
            }

        });

//        binding.swichStatic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    binding.clStatic.setVisibility(View.INVISIBLE);
//                } else {
//                    binding.clStatic.setVisibility(View.VISIBLE);
//                }
//            }

//        });


        binding.networkConfirmTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LoadingDialogUtil.getInstance().show(getContext(), getString(R.string.toast_tips3));
                PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE
                        , "USB"
                        , new INeoPrinterCallback() {
                            @Override
                            public void onRunResult(boolean b) throws RemoteException {

                            }

                            @Override
                            public void onReturnString(String s) throws RemoteException {

                            }

                            @Override
                            public void onRaiseException(int i, String s) throws RemoteException {

                            }

                            @Override
                            public void onPrintResult(int i, String s) throws RemoteException {

                            }
                        });


                String selectedWifi = binding.ssidSpinner.getText().toString().trim();
                if (!Utils.isEmpty(selectedWifi)) {
                    // 解析SSID和BSSID
                    if (selectedWifi.contains(" (")) {
                        String[] parts = selectedWifi.split(" \\(");
                        String ssid = parts[0];
                        String bssid = parts[1].replace(")", "");
                        String pwd = binding.pwdEt.getText().toString().trim();
                        if (!bssid.contains("OPEN")) {
                            if (Utils.isEmpty(pwd)) {
                                Toast.makeText(getContext(), getString(R.string.toast1), Toast.LENGTH_SHORT).show();

                                LoadingDialogUtil.getInstance().hide();
                                return;
                            }
                        }
                        if (bssid.contains("OPEN")) {
                            pwd = "";
                        }
                        Log.d(TAG, "WIFI_IP_TYPE ssid=>" + ssid + "  pwd=" + pwd);
                        if (binding.swichStatic.isChecked()) {
                            connectToWifi(ssid, pwd);
                        } else {
//                        conectStaticWifi(ssid, pwd);
                        }
                    }
                } else {
                    LoadingDialogUtil.getInstance().hide();

                    Toast.makeText(getContext(), getText(R.string.tips1), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.ipConfirmTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LoadingDialogUtil.getInstance().show(v.getContext(), getString(R.string.toast_tips2));
                PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE
                        , "USB"
                        , new INeoPrinterCallback() {
                            @Override
                            public void onRunResult(boolean b) throws RemoteException {

                            }

                            @Override
                            public void onReturnString(String s) throws RemoteException {

                            }

                            @Override
                            public void onRaiseException(int i, String s) throws RemoteException {

                            }

                            @Override
                            public void onPrintResult(int i, String s) throws RemoteException {

                            }
                        });

                if (!outoConnect) {
                    String ip = binding.ipEt.getText().toString().trim();
                    if (Utils.isEmpty(ip)) {
                        Toast.makeText(getContext(), getText(R.string.ip_tips), Toast.LENGTH_SHORT).show();
                        LoadingDialogUtil.getInstance().hide();
                        return;
                    }
                    if (!NetworkValidator.validateIP(ip)) {
                        Toast.makeText(getContext(), getText(R.string.ip_tips), Toast.LENGTH_SHORT).show();
                        LoadingDialogUtil.getInstance().hide();
                        return;
                    }

                    PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIFI_CONNECT_IP
                            , ip
                            , new INeoPrinterCallback() {
                                @Override
                                public void onRunResult(boolean b) throws RemoteException {

                                }

                                @Override
                                public void onReturnString(String s) throws RemoteException {

                                }

                                @Override
                                public void onRaiseException(int i, String s) throws RemoteException {

                                }

                                @Override
                                public void onPrintResult(int i, String s) throws RemoteException {
                                    Log.d(TAG, "WIFI_CONNECT=>" + s + "  i=" + i);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LoadingDialogUtil.getInstance().hide();
                                            if (i == 1) {
                                                MainActivity.connectAddress = s;
                                                MainActivity.ipConnect = s;
                                                MainActivity.connectType = "WIFI";
                                                MainActivity.connectContent = binding.wifiIPTv.getText().toString().trim();

                                                switchFragment(4);

                                                PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE
                                                        , "WIFI"
                                                        , new INeoPrinterCallback() {
                                                            @Override
                                                            public void onRunResult(boolean b) throws RemoteException {

                                                            }

                                                            @Override
                                                            public void onReturnString(String s) throws RemoteException {

                                                            }

                                                            @Override
                                                            public void onRaiseException(int i, String s) throws RemoteException {

                                                            }

                                                            @Override
                                                            public void onPrintResult(int i, String s) throws RemoteException {

                                                            }
                                                        });

                                            } else {
                                                ToastUtil.showShort(getContext(), R.string.connect_fail);
                                            }
                                        }
                                    });

                                }
                            });


                } else {

                    if (!Utils.isEmpty(baseIp)) {
                        MainActivity.connectAddress = baseIp;
                        MainActivity.ipConnect = baseIp;
                    }
                    if (Utils.isEmpty(MainActivity.ipConnect)) {
                        ToastUtil.showShort(v.getContext(), R.string.connect_fail_tips);
                        LoadingDialogUtil.getInstance().hide();
                        return;
                    }
                    Log.d(TAG, "ipConfirmTv. ipConnect=>" + MainActivity.ipConnect + " , wifiIPTv=" + binding.wifiIPTv);

                    PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIFI_CONNECT_IP
                            , MainActivity.ipConnect
                            , new INeoPrinterCallback() {
                                @Override
                                public void onRunResult(boolean b) throws RemoteException {

                                }

                                @Override
                                public void onReturnString(String s) throws RemoteException {

                                }

                                @Override
                                public void onRaiseException(int i, String s) throws RemoteException {

                                }

                                @Override
                                public void onPrintResult(int i, String s) throws RemoteException {
                                    Log.d(TAG, "WIFI_CONNECT=>" + s + "  i=" + i);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LoadingDialogUtil.getInstance().hide();
                                            if (i == 1) {
                                                baseIp = "";
                                                binding.baseIPTv.setText("");
                                                binding.baseIPTv.setVisibility(View.INVISIBLE);

                                                MainActivity.connectAddress = s;
                                                MainActivity.ipConnect = s;
                                                MainActivity.connectType = "WIFI";
                                                MainActivity.connectContent = binding.wifiIPTv.getText().toString().trim();

                                                PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE
                                                        , "WIFI"
                                                        , new INeoPrinterCallback() {
                                                            @Override
                                                            public void onRunResult(boolean b) throws RemoteException {

                                                            }

                                                            @Override
                                                            public void onReturnString(String s) throws RemoteException {

                                                            }

                                                            @Override
                                                            public void onRaiseException(int i, String s) throws RemoteException {

                                                            }

                                                            @Override
                                                            public void onPrintResult(int i, String s) throws RemoteException {

                                                            }
                                                        });

                                                switchFragment(4);
                                            } else {
                                                getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                                        s, Toast.LENGTH_LONG).show());
                                            }
                                        }
                                    });


                                }
                            });


                }
            }
        });

        binding.getConnectIPTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LoadingDialogUtil.getInstance().show(getContext(), getString(R.string.toast_tips1));

                PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE
                        , "USB"
                        , new INeoPrinterCallback() {
                            @Override
                            public void onRunResult(boolean b) throws RemoteException {

                            }

                            @Override
                            public void onReturnString(String s) throws RemoteException {

                            }

                            @Override
                            public void onRaiseException(int i, String s) throws RemoteException {

                            }

                            @Override
                            public void onPrintResult(int i, String s) throws RemoteException {

                            }
                        });

                checkWifi = 10;
                baseIp = "";
                binding.baseIPTv.setText("");
                binding.baseIPTv.setVisibility(View.INVISIBLE);
                checkWifiConnect();

            }
        });
        binding.viewTitle.setRightCallback(v -> {
            Log.d(TAG, "setting: ");
            switchFragment(100);
        });

        binding.btDisconnect.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                disConnect();
            }
        });

    }

    //连接静态ip
//    private void conectStaticWifi(String ssid, String pwd) {
//        //设置当前动态还是静态
//        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP_TYPE).setConfig(binding.swichStatic.isChecked() ? IpType.DHCP.getTypeName() : IpType.STATIC.getTypeName()), new IWirelessPrintResult.Stub() {
//            @Override
//            public void onResult(int i, String s) throws RemoteException {
//                Log.d(TAG, "WIFI_IP_TYPE =>" + s + "  i=" + i);
//            }
//
//            @Override
//            public void onReturnString(String s) throws RemoteException {
//
//            }
//        });
//        //配网
//        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIRELESS_NET_SETUP).setSsid(ssid).setPwd(pwd), new IWirelessPrintResult.Stub() {
//            @Override
//            public void onResult(int i, String s) throws RemoteException {
//                Log.d(TAG, "配网=>" + s + "  i=" + i);
//            }
//
//            @Override
//            public void onReturnString(String s) throws RemoteException {
//
//            }
//
//
//        });
//
//        String ip = binding.staticIpEt.getText().toString().trim();
//        String mask = binding.maskEt.getText().toString().trim();
//        String gw = binding.gwEt.getText().toString().trim();
//        String dns = binding.dnsEt.getText().toString().trim();
//        if (Utils.isEmpty(ip) || Utils.isEmpty(mask) || Utils.isEmpty(gw) || Utils.isEmpty(dns)) {
//            Toast.makeText(getActivity(), getText(R.string.ip_tips2), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!NetworkValidator.validateGateway(ip, gw, mask)) {
//            Toast.makeText(getActivity(), getText(R.string.ip_tips3), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!NetworkValidator.validateDNS(dns)) {
//            Toast.makeText(getActivity(), getText(R.string.ip_tips4), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP).setConfig(ip), new IWirelessPrintResult.Stub() {
//            @Override
//            public void onResult(int i, String s) throws RemoteException {
//                Log.d(TAG, "set ip=>" + s + "  i=" + i);
//            }
//
//            @Override
//            public void onReturnString(String s) throws RemoteException {
//                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", s));
//            }
//
//
//        });
//
//        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_MASK).setConfig(mask), new IWirelessPrintResult.Stub() {
//            @Override
//            public void onResult(int i, String s) throws RemoteException {
//
//            }
//
//            @Override
//            public void onReturnString(String s) throws RemoteException {
//
//            }
//
//
//        });
//        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_GW).setConfig(gw), new IWirelessPrintResult.Stub() {
//            @Override
//            public void onResult(int i, String s) throws RemoteException {
//                Log.d(TAG, "gw=>" + s + "  i=" + i);
//            }
//
//            @Override
//            public void onReturnString(String s) throws RemoteException {
//
//            }
//
//
//        });
//        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_DNS).setConfig(dns), new IWirelessPrintResult.Stub() {
//            @Override
//            public void onResult(int i, String s) throws RemoteException {
//                Log.d(TAG, "dns=>" + s + "  i=" + i);
//            }
//
//            @Override
//            public void onReturnString(String s) throws RemoteException {
//
//            }
//
//
//        });
//
//        PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
//                        .setWirelessStyle(WirelessConfig.WIFI_IP)
//                , new IWirelessPrintResult.Stub() {
//                    @Override
//                    public void onResult(int i, String s) throws RemoteException {
//
//                    }
//
//                    @Override
//                    public void onReturnString(String s) throws RemoteException {
//                        Log.d(TAG, "ip回调==: s= " + s);
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!Utils.isEmpty(s) && !s.equals("-1")){
//                                    binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green60_corner_5));
//                                    binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
//                                    binding.clConnectNetwork.setVisibility(View.INVISIBLE);
//                                    binding.clConnectIP.setVisibility(View.VISIBLE);
//
//                                    binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), s));
//                                }else {
//                                    ToastUtil.showShort(getContext(),R.string.connect_wifi_tips1);
//                                }
//
//                            }
//                        });
//
//                    }
//                });
//
//    }

    private void connectToWifi(String ssid, String pwd) {
        // 这里需要实现具体的WiFi连接逻辑
        Log.d(TAG, "正在连接: " + ssid);

        PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIFI_DHCP
                , binding.swichStatic.isChecked() ? "IpType" : "STATIC"
                , new INeoPrinterCallback() {
                    @Override
                    public void onRunResult(boolean b) throws RemoteException {

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {

                    }

                    @Override
                    public void onRaiseException(int i, String s) throws RemoteException {

                    }

                    @Override
                    public void onPrintResult(int i, String s) throws RemoteException {
                        Log.d(TAG, "WIFI_IP_TYPE =>" + s + "  i=" + i);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (i < 1) {
                                    LoadingDialogUtil.getInstance().hide();
                                    ToastUtil.showShort(getContext(), R.string.connect_fail);
                                } else {
                                    List<String> list1 = new ArrayList<>();
                                    list1.add("USB");
                                    list1.add(ssid);
                                    list1.add(pwd);

                                    PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIFI_SETUP_NET
                                            , list1, new INeoPrinterCallback() {
                                                @Override
                                                public void onRunResult(boolean b) throws RemoteException {

                                                }

                                                @Override
                                                public void onReturnString(String s) throws RemoteException {

                                                }

                                                @Override
                                                public void onRaiseException(int i, String s) throws RemoteException {

                                                }

                                                @Override
                                                public void onPrintResult(int i, String s) throws RemoteException {
                                                    Log.d(TAG, "配网回调==: i= " + i + " ,s=>" + s);
                                                    if (i == 1) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.connected)));
                                                            }
                                                        });

                                                        retry = 10;
                                                        sendSsid();

                                                    } else {
                                                        //失败
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                LoadingDialogUtil.getInstance().hide();
                                                                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.un_connected)));
                                                                ToastUtil.showShort(getContext(), R.string.set_fail);
                                                            }
                                                        });
                                                    }
                                                }
                                            });


                                }
                            }
                        });
                    }
                });

    }

    int retry = 10;

    private void sendSsid() {
        Log.d(TAG, "retry=>" + retry);
        PrinterHelper.getInstance().getPrinterInfo(WifiKeyName.WIRELESS_CONNECT_STATUS
                , new INeoPrinterCallback() {
                    @Override
                    public void onRunResult(boolean b) throws RemoteException {

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {
                        Log.d(TAG, "WIFI_CONNECT_STATUS==: s= " + s);
                        if (!Utils.isEmpty(s)) {
                            if (s.equals("0") || s.equals("2")) {
                                retry--;
                                if (retry > 0) {
                                    SystemClock.sleep(1000);
                                    sendSsid();
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.un_connected)));
                                            binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), "------"));

                                            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green60_corner_5));
                                            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                            binding.clConnectNetwork.setVisibility(View.VISIBLE);
                                            binding.clConnectIP.setVisibility(View.INVISIBLE);

                                            LoadingDialogUtil.getInstance().hide();


                                        }
                                    });
                                    retry = 10;

                                }

                            } else if (s.equals("-1")) {
                                checkWifi = 10;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LoadingDialogUtil.getInstance().hide();
                                        ToastUtil.showShort(getContext(), R.string.get_fail);

                                    }
                                });

                            } else {
                                retry = 10;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.connected)));
                                    }
                                });


                                PrinterHelper.getInstance().getPrinterInfo(WifiKeyName.WIFI_IP
                                        , new INeoPrinterCallback() {
                                            @Override
                                            public void onRunResult(boolean b) throws RemoteException {

                                            }

                                            @Override
                                            public void onReturnString(String s) throws RemoteException {
                                                Log.d(TAG, "ip回调==: s= " + s);

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {


                                                        if (!Utils.isEmpty(s) && !s.equals("-1")) {

                                                            binding.baseIPTv.setVisibility(View.INVISIBLE);
                                                            baseIp = "";
                                                            binding.baseIPTv.setText("");

                                                            MainActivity.ipConnect = s;
                                                            MainActivity.connectAddress = s;
                                                            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green60_corner_5));
                                                            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                                            binding.clConnectNetwork.setVisibility(View.INVISIBLE);
                                                            binding.clConnectIP.setVisibility(View.VISIBLE);

                                                            binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), s));
                                                            ToastUtil.showShort(getContext(), R.string.connect_wifi_tips2);
                                                        } else {
                                                            ToastUtil.showShort(getContext(), R.string.connect_wifi_tips1);
                                                        }

                                                        LoadingDialogUtil.getInstance().hide();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onRaiseException(int i, String s) throws RemoteException {

                                            }

                                            @Override
                                            public void onPrintResult(int i, String s) throws RemoteException {

                                            }
                                        });

                            }
                        }
                    }

                    @Override
                    public void onRaiseException(int i, String s) throws RemoteException {

                    }

                    @Override
                    public void onPrintResult(int i, String s) throws RemoteException {

                    }
                });

    }

    public void initData() {
        Log.d(TAG, "initData==: " + MainActivity.ipConnect + "  ,打印机状态==》   " + PrinterHelper.getInstance().getPrinterStatus());

        baseIp = "";
        binding.baseIPTv.setText("");
        binding.baseIPTv.setVisibility(View.INVISIBLE);

        ExecutorServiceManager.cancelTask(wifiListFuture);
        wifiListFuture = ExecutorServiceManager.getExecutorService().submit(() -> {
            try {
                startWifiScan();
            } catch (Exception e) {
                Log.e(TAG, "Error playing audio: " + e.getMessage());
            }
        });
        if (Utils.isEmpty(MainActivity.ipConnect)) {//判断SDK 是否有连接

            PrinterHelper.getInstance().getPrinterInfo(WifiKeyName.WIFI_CURRENT_CONNECT_IP
                    , new INeoPrinterCallback() {
                        @Override
                        public void onRunResult(boolean b) throws RemoteException {

                        }

                        @Override
                        public void onReturnString(String s) throws RemoteException {
                            Log.d(TAG, "initData==CURRENT_CONNECT_WIFI_IP: " + s + "  ,i=>  " );
                            if (!Utils.isEmpty(s)) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (s != null) {
                                            MainActivity.ipConnect = s;
                                            MainActivity.connectAddress = s;
                                            MainActivity.connectType = "WIFI";
                                            binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI"
                                                    , getString(R.string.connected)));
                                            binding.wifiIPTv.setText(String.format(getString(R.string.status_ip)
                                                    , MainActivity.ipConnect));

                                            LoadingDialogUtil.getInstance().hide();
                                            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green60_corner_5));
                                            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                            binding.clConnectNetwork.setVisibility(View.INVISIBLE);
                                            binding.clConnectIP.setVisibility(View.VISIBLE);

                                            binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), s));
                                        }
                                    }
                                });


                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.un_connected)));
                                        binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), "------"));

                                        binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green60_corner_5));
                                        binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                        binding.clConnectNetwork.setVisibility(View.VISIBLE);
                                        binding.clConnectIP.setVisibility(View.INVISIBLE);

                                    }
                                });
                            }
                        }

                        @Override
                        public void onRaiseException(int i, String s) throws RemoteException {

                        }

                        @Override
                        public void onPrintResult(int i, String s) throws RemoteException {

                        }
                    });

        } else {

            updateUi();

        }

        updatePrinterStatus(PrinterHelper.getInstance().getPrinterStatus());

    }


    public void disConnect() {

        PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIFI_DISCONNECT
                , ""
                , new INeoPrinterCallback() {
                    @Override
                    public void onRunResult(boolean b) throws RemoteException {

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {

                    }

                    @Override
                    public void onRaiseException(int i, String s) throws RemoteException {

                    }

                    @Override
                    public void onPrintResult(int i, String s) throws RemoteException {
                        Log.d(TAG, "DISCONNECT_WIFI==:  " + s);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI"
                                        , getString(R.string.un_connected)));
                                binding.wifiIPTv.setText(String.format(getString(R.string.status_ip)
                                        , "-------"));
                                MainActivity.ipConnect = "";
                            }
                        });

                    }
                });

    }

    public void updateUi() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI"
                        , getString(R.string.connected)));
                binding.wifiIPTv.setText(String.format(getString(R.string.status_ip)
                        , MainActivity.ipConnect));
                binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green60_corner_5));
                binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                binding.clConnectNetwork.setVisibility(View.INVISIBLE);
                binding.clConnectIP.setVisibility(View.VISIBLE);
                Log.d(TAG, "   " + binding.clConnectIP.getVisibility() + "   " + binding.clConnectNetwork.getVisibility());
            }
        });
    }


    int checkWifi = 10;

    private void checkWifiConnect() {
        Log.d(TAG, "checkWifi==:  " + checkWifi);

        PrinterHelper.getInstance().getPrinterInfo(WifiKeyName.WIRELESS_CONNECT_STATUS
                , new INeoPrinterCallback() {
                    @Override
                    public void onRunResult(boolean b) throws RemoteException {

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {
                        Log.d(TAG, "WIFI_CONNECT_STATUS==: s= " + s);//1已连接  0是没连接
                        if (!Utils.isEmpty(s)) {
                            if (s.equals("0") || s.equals("2")) {
                                checkWifi--;
                                if (checkWifi > 0) {
                                    SystemClock.sleep(2000);
                                    checkWifiConnect();
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LoadingDialogUtil.getInstance().hide();
                                            ToastUtil.showShort(getContext(), R.string.get_fail);

                                        }
                                    });
                                    checkWifi = 10;

                                }

                            } else if (s.equals("-1")) {
                                checkWifi = 10;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        ToastUtil.showShort(getContext(), R.string.get_fail);
                                        LoadingDialogUtil.getInstance().hide();

                                    }
                                });

                            } else {
                                checkWifi = 10;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.connected)));
                                    }
                                });

                                PrinterHelper.getInstance().getPrinterInfo(WifiKeyName.WIFI_IP
                                        , new INeoPrinterCallback() {
                                            @Override
                                            public void onRunResult(boolean b) throws RemoteException {

                                            }

                                            @Override
                                            public void onReturnString(String s) throws RemoteException {
                                                Log.d(TAG, "ip回调==: s= " + s);


                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        LoadingDialogUtil.getInstance().hide();
                                                        if (!Utils.isEmpty(s) && !s.equals("-1")) {
                                                            baseIp = s;
                                                            binding.baseIPTv.setVisibility(View.VISIBLE);
                                                            binding.baseIPTv.setText(String.format(getString(R.string.status_ip), s));

                                                        } else {
                                                            ToastUtil.showShort(getContext(), R.string.connect_wifi_tips1);
                                                        }

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onRaiseException(int i, String s) throws RemoteException {

                                            }

                                            @Override
                                            public void onPrintResult(int i, String s) throws RemoteException {

                                            }
                                        });

                            }
                        }else {
                            checkWifi = 10;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ToastUtil.showShort(getContext(), R.string.get_fail);
                                    LoadingDialogUtil.getInstance().hide();

                                }
                            });
                        }
                    }

                    @Override
                    public void onRaiseException(int i, String s) throws RemoteException {

                    }

                    @Override
                    public void onPrintResult(int i, String s) throws RemoteException {

                    }
                });

    }


    public void updatePrinterStatus(int status) {
        Log.d(TAG, "updatePrinterStatus: " + status + "  ,  " + getUserVisibleHint() + " ,isVisibleToView= " + isVisibleToView);

        if (binding != null) {
            if (status != 0) {
//                ToastUtil.showShort(getContext(), R.string.connect_wifi_tips1);
                baseIp = "";
                binding.baseIPTv.setVisibility(View.INVISIBLE);
                binding.baseIPTv.setText("");
                binding.networkConfirmTv.setEnabled(false);
                binding.networkConfirmTv.setAlpha(0.5f);
                binding.ipConfirmTv.setEnabled(false);
                binding.ipConfirmTv.setAlpha(0.5f);
                binding.getConnectIPTv.setEnabled(false);
                binding.getConnectIPTv.setAlpha(0.5f);
                LoadingDialogUtil.getInstance().hide();
            } else {
                binding.networkConfirmTv.setEnabled(true);
                binding.networkConfirmTv.setAlpha(1f);
                binding.ipConfirmTv.setEnabled(true);
                binding.ipConfirmTv.setAlpha(1f);
                binding.getConnectIPTv.setEnabled(true);
                binding.getConnectIPTv.setAlpha(1f);
            }
        }
    }

    private SwitchFragmentListener fragmentListener;

    public void setCallback(SwitchFragmentListener listener) {

        fragmentListener = listener;
        Log.d(TAG, "setCallback: " + (fragmentListener != null));
    }


    public void switchFragment(int num) {
        Log.d(TAG, "switchPager :num=  " + num + (fragmentListener != null));

        if (fragmentListener != null) {
            fragmentListener.switchFragment(num);
        }
    }

    public void setSpinnerData(ArrayList<String> wifiList) {
        Log.d(TAG, "setSpinnerData=  " + wifiList);
        this.list = wifiList;
        if (list != null && list.size() > 0) {
            binding.ssidSpinner.setText(list.get(0));
        }
    }


    @Override
    public void onWifiListUpdated(ArrayList<String> list) {
        getActivity().runOnUiThread(() -> {
            Log.e(TAG, "onWifiListUpdated results: " + (list == null ? null : list.size()));

            setSpinnerData(list);
        });

    }

    @Override
    public void onPermissionRequired() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                requestPermissionCode);
    }

    @Override
    public void onPermissionDenied() {
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                "Location permission denied", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onWifiDisabled() {
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                "Please enable WiFi", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onScanFailed() {
//        getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
//                "WiFi scan failed", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onWifiConnectStatus(boolean b) {
        Log.e(TAG, "onWifiConnectStatus results: " + b);

        if (!b) {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).disConnectWirelessPrint();
            }
            disConnect();
        }
    }

    @Override
    public void onDestroyView() {
        if (wifiScanner != null) {
            wifiScanner.release();
        }
        ExecutorServiceManager.shutdownExecutorService();
        super.onDestroyView();
    }

    PopupWindow popupWindow = null;

    private void showPopup(Context context, List<String> mList, View view) {

        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        // 加载布局
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_list, null);

        // 初始化ListView
        ListView listView = popupView.findViewById(R.id.listView);

        listView.setAdapter(new ListAdapter(getContext(), mList));

        // 列表项点击事件
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            //Toast.makeText(context, "点击了: " + mList.get(position), Toast.LENGTH_SHORT).show();
            try {
                if (mList != null && mList.size() > position) {
                    binding.ssidSpinner.setText(mList.get(position));
                }

            } catch (Exception e) {
                Log.d(TAG, "  " + e.getMessage());
            }

            if (popupWindow != null) {
                popupWindow.dismiss();
            }

        });

        // 创建PopupWindow（宽度填满屏幕，高度包裹内容）
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // 允许获取焦点
        );

        // 关键配置：设置背景（必须设置才能响应外部点击）
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 允许外部点击关闭
        popupWindow.setOutsideTouchable(true);

        // 设置动画效果
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

        // 显示在按钮下方
        popupWindow.showAsDropDown(view);
    }

}
