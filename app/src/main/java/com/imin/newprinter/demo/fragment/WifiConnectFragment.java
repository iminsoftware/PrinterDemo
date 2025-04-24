package com.imin.newprinter.demo.fragment;

import static com.imin.newprinter.demo.MainActivity.requestPermissionCode;
import static me.goldze.mvvmhabit.utils.Utils.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.imin.newprinter.demo.MainActivity;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentWifiConnectBinding;
import com.imin.newprinter.demo.utils.LoadingDialogUtil;
import com.imin.newprinter.demo.utils.NetworkValidator;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.newprinter.demo.utils.WifiScannerHelper;
import com.imin.newprinter.demo.utils.WifiScannerSingleton;
import com.imin.printer.IWirelessPrintResult;
import com.imin.printer.PrinterHelper;
import com.imin.printer.enums.ConnectType;
import com.imin.printer.enums.IpType;
import com.imin.printer.enums.WirelessConfig;
import com.imin.printer.wireless.WirelessPrintStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser + "    " + isResumed());
        if (isVisibleToUser && isResumed()) {
            // 当 Fragment 对用户可见时执行操作（兼容旧版本）
            //loadData();
            initData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + getUserVisibleHint());

    }

    WifiScannerSingleton wifiScanner;

    private void startWifiScan() {
        wifiScanner.startWifiScan(this);
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

            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
            binding.clConnectNetwork.setVisibility(View.VISIBLE);
            binding.clConnectIP.setVisibility(View.INVISIBLE);
        });

        binding.connectTv.setOnClickListener(v -> {

            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
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

        });
        binding.ipLy.setOnClickListener(view -> {
            binding.ipIv.setImageResource(R.drawable.ic_check);
            binding.autoConnectIv.setImageResource(R.drawable.ic_uncheck);
            binding.ipEt.setEnabled(true);
            outoConnect = false;
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

        binding.networkConfirmTv.setOnClickListener(view -> {
//            if (binding.ssidSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION) {

            PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                    .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                    .setConfig(ConnectType.USB.getTypeName()), new IWirelessPrintResult.Stub() {
                @Override
                public void onResult(int i, String s) throws RemoteException {

                }

                @Override
                public void onReturnString(String s) throws RemoteException {

                }
            });

            binding.networkConfirmTv.setEnabled(false);
            binding.networkConfirmTv.setAlpha(0.5f);
            String selectedWifi = binding.ssidSpinner.getText().toString().trim();
            if (!Utils.isEmpty(selectedWifi)) {
                // 解析SSID和BSSID
                if (selectedWifi.contains(" (")) {
                    String[] parts = selectedWifi.split(" \\(");
                    String ssid = parts[0];
                    String bssid = parts[1].replace(")", "");
                    String pwd = binding.pwdEt.getText().toString().trim();
                    if (!bssid.equals("OPEN")) {
                        if (Utils.isEmpty(pwd)) {
                            Toast.makeText(getContext(), getString(R.string.toast1), Toast.LENGTH_SHORT).show();
                            binding.networkConfirmTv.setEnabled(true);
                            binding.networkConfirmTv.setAlpha(1f);
                            return;
                        }
                    }
                    Log.d(TAG, "WIFI_IP_TYPE ssid=>" + ssid + "  pwd=" + pwd);
                    if (binding.swichStatic.isChecked()) {
                        connectToWifi(ssid, pwd);
                    } else {
                        conectStaticWifi(ssid, pwd);
                    }

//                    showConnectionDialog(ssid, bssid);
                }
            } else {
                binding.networkConfirmTv.setEnabled(true);
                binding.networkConfirmTv.setAlpha(1f);
                Toast.makeText(getContext(), getText(R.string.tips1), Toast.LENGTH_SHORT).show();
            }

//            } else {
//                Toast.makeText(getContext(), "请先选择WiFi网络", Toast.LENGTH_SHORT).show();
//            }
        });

        binding.ipConfirmTv.setOnClickListener(v -> {
            LoadingDialogUtil.getInstance().show(v.getContext(),"");
            if (!outoConnect) {
                String ip = binding.ipEt.getText().toString().trim();
                if (Utils.isEmpty(ip)) {
                    Toast.makeText(getContext(), getText(R.string.ip_tips), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!NetworkValidator.validateIP(ip)) {
                    Toast.makeText(getContext(), getText(R.string.ip_tips), Toast.LENGTH_SHORT).show();
                    return;
                }
                MainActivity.connectAddress = ip;
                MainActivity.ipConnect = ip;



                PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIFI_CONNECT_IP)
                        .setConfig(MainActivity.connectAddress), new IWirelessPrintResult.Stub() {
                    @Override
                    public void onResult(int i, String s) throws RemoteException {
                        Log.d(TAG, "WIFI_CONNECT=>" + s + "  i=" + i);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialogUtil.getInstance().hide();
                                if (i == 0) {
                                    MainActivity.connectType = "WIFI";
                                    MainActivity.connectContent = binding.wifiIPTv.getText().toString().trim();
                                    switchFragment(4);

                                    PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                                            .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                                            .setConfig(ConnectType.WIFI.getTypeName()), new IWirelessPrintResult.Stub() {
                                        @Override
                                        public void onResult(int i, String s) throws RemoteException {

                                        }

                                        @Override
                                        public void onReturnString(String s) throws RemoteException {

                                        }
                                    });
                                }
                            }
                        });

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {
                        Log.d(TAG, "WIFI_CONNECT =>" + s);
                    }
                });


            }else {


                PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIFI_AUTO_CONNECT_IP)
                        .setConfig(MainActivity.connectAddress), new IWirelessPrintResult.Stub() {
                    @Override
                    public void onResult(int i, String s) throws RemoteException {
                        Log.d(TAG, "WIFI_CONNECT=>" + s + "  i=" + i);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialogUtil.getInstance().hide();
                                if (i == 0) {
                                    MainActivity.connectAddress = s;
                                    MainActivity.ipConnect = s;
                                    MainActivity.connectType = "WIFI";
                                    MainActivity.connectContent = binding.wifiIPTv.getText().toString().trim();

                                    PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                                            .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                                            .setConfig(ConnectType.WIFI.getTypeName()), new IWirelessPrintResult.Stub() {
                                        @Override
                                        public void onResult(int i, String s) throws RemoteException {

                                        }

                                        @Override
                                        public void onReturnString(String s) throws RemoteException {

                                        }
                                    });

                                    switchFragment(4);
                                }else {
                                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                            s, Toast.LENGTH_LONG).show());
                                }
                            }
                        });

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {
                        Log.d(TAG, "WIFI_CONNECT =>" + s);
                    }
                });



            }



        });

        binding.viewTitle.setRightCallback(v -> {
            Log.d(TAG, "setting: ");
            switchFragment(100);
        });

        binding.btDisconnect.setOnClickListener(view -> {
            PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                    .setWirelessStyle(WirelessConfig.DISCONNECT_WIFI), new IWirelessPrintResult.Stub() {
                @Override
                public void onResult(int i, String s) throws RemoteException {
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

                @Override
                public void onReturnString(String s) throws RemoteException {

                }
            });
        });

    }

    //连接静态ip
    private void conectStaticWifi(String ssid, String pwd) {
        //设置当前动态还是静态
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP_TYPE).setConfig(binding.swichStatic.isChecked() ? IpType.DHCP.getTypeName() : IpType.STATIC.getTypeName()), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG, "WIFI_IP_TYPE =>" + s + "  i=" + i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }
        });
        //配网
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIRELESS_NET_SETUP).setSsid(ssid).setPwd(pwd), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG, "配网=>" + s + "  i=" + i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }


        });

        String ip = binding.staticIpEt.getText().toString().trim();
        String mask = binding.maskEt.getText().toString().trim();
        String gw = binding.gwEt.getText().toString().trim();
        String dns = binding.dnsEt.getText().toString().trim();
        if (Utils.isEmpty(ip) || Utils.isEmpty(mask) || Utils.isEmpty(gw) || Utils.isEmpty(dns)) {
            Toast.makeText(getActivity(), getText(R.string.ip_tips2), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkValidator.validateGateway(ip, gw, mask)) {
            Toast.makeText(getActivity(), getText(R.string.ip_tips3), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkValidator.validateDNS(dns)) {
            Toast.makeText(getActivity(), getText(R.string.ip_tips4), Toast.LENGTH_SHORT).show();
            return;
        }

        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP).setConfig(ip), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG, "set ip=>" + s + "  i=" + i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {
                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", s));
            }


        });

        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_MASK).setConfig(mask), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {

            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }


        });
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_GW).setConfig(gw), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG, "gw=>" + s + "  i=" + i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }


        });
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_DNS).setConfig(dns), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG, "dns=>" + s + "  i=" + i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }


        });

        PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIFI_IP)
                , new IWirelessPrintResult.Stub() {
                    @Override
                    public void onResult(int i, String s) throws RemoteException {

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {
                        Log.d(TAG, "ip回调==: s= " + s);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (s != null) {
                                    binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
                                    binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                    binding.clConnectNetwork.setVisibility(View.INVISIBLE);
                                    binding.clConnectIP.setVisibility(View.VISIBLE);

                                    binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), s));
                                }
                            }
                        });

                    }
                });

    }

    private void connectToWifi(String ssid, String pwd) {
        // 这里需要实现具体的WiFi连接逻辑
        Log.d(TAG, "正在连接: " + ssid);
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP_TYPE).setConfig(binding.swichStatic.isChecked() ? IpType.DHCP.getTypeName() : IpType.STATIC.getTypeName()), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG, "WIFI_IP_TYPE =>" + s + "  i=" + i);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialogUtil.getInstance().show(getContext(), "");
                    }
                });

                PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIRELESS_NET_SETUP)
                        .setSsid(ssid)
                        .setPwd(pwd), new IWirelessPrintResult.Stub() {
                    @Override
                    public void onResult(int i, String s) throws RemoteException {
                        Log.d(TAG, "配网回调==: i= " + i + " ,s=>" + s);
                        if (i == 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.connected)));
                                }
                            });

                            retry=5;
                            sendSsid();

                        } else {


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.networkConfirmTv.setEnabled(true);
                                    binding.networkConfirmTv.setAlpha(1f);
                                    LoadingDialogUtil.getInstance().hide();
                                    binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.un_connected)));
                                }
                            });
                        }
                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {
                        Log.d(TAG, "配网回调==: s= " + s);

                    }
                });



            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }
        });

    }

    int retry=5;
    private void sendSsid() {
        Log.d(TAG, "retry=>" +retry);
        PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIFI_CONNECT_STATUS)
                , new IWirelessPrintResult.Stub() {
                    @Override
                    public void onResult(int i, String s) throws RemoteException {
                        Log.d(TAG, "WIFI_CONNECT_STATUS==: i= " + i + " ,s=>" + s);

                    }

                    @Override
                    public void onReturnString(String s) throws RemoteException {
                        Log.d(TAG, "WIFI_CONNECT_STATUS==: s= " + s);
                        if (!Utils.isEmpty(s)) {
                            if (s.equals("0") || s.equals("2")) {
                                retry--;
                                if (retry >0){
                                    SystemClock.sleep(2000);
                                    sendSsid();
                                }else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.un_connected)));
                                            binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), "------"));

                                            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
                                            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                            binding.clConnectNetwork.setVisibility(View.VISIBLE);
                                            binding.clConnectIP.setVisibility(View.INVISIBLE);

                                            LoadingDialogUtil.getInstance().hide();
                                            binding.networkConfirmTv.setEnabled(true);
                                            binding.networkConfirmTv.setAlpha(1f);

                                        }
                                    });
                                    retry = 5;

                                }

                            } else {
                                retry = 5;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.connected)));
                                    }
                                });

                                PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                                                .setWirelessStyle(WirelessConfig.WIFI_IP)
                                        , new IWirelessPrintResult.Stub() {
                                            @Override
                                            public void onResult(int i, String s) throws RemoteException {
                                                Log.d(TAG, "请求回去==: i= " + i + " ,s=>" + s);

                                            }

                                            @Override
                                            public void onReturnString(String s) throws RemoteException {
                                                Log.d(TAG, "ip回调==: s= " + s);

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        LoadingDialogUtil.getInstance().hide();
                                                        binding.networkConfirmTv.setEnabled(true);
                                                        binding.networkConfirmTv.setAlpha(1f);
                                                        if (s != null) {
                                                            MainActivity.ipConnect = s;
                                                            MainActivity.connectAddress = s;
                                                            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
                                                            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                                            binding.clConnectNetwork.setVisibility(View.INVISIBLE);
                                                            binding.clConnectIP.setVisibility(View.VISIBLE);

                                                            binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), s));
                                                        }
                                                    }
                                                });

                                            }
                                        });
                            }
                        }

                    }
                });
    }

    public void initData() {
        Log.d(TAG, "initData==: " + MainActivity.ipConnect);
        binding.networkConfirmTv.setEnabled(true);
        binding.networkConfirmTv.setAlpha(1f);

        if (Utils.isEmpty(MainActivity.ipConnect)) {//判断SDK 是否有连接
            startWifiScan();
            PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                    .setWirelessStyle(WirelessConfig.CURRENT_CONNECT_WIFI_IP), new IWirelessPrintResult.Stub() {
                @Override
                public void onResult(int i, String s) throws RemoteException {
                    Log.d(TAG, "initData==CURRENT_CONNECT_WIFI_IP: " + MainActivity.ipConnect);
                    if (i == 0) {
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
                                    MainActivity.connectAddress = s;
                                    binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
                                    binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                    binding.clConnectNetwork.setVisibility(View.INVISIBLE);
                                    binding.clConnectIP.setVisibility(View.VISIBLE);

                                    binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), s));
                                }
                            }
                        });



                    } else {
//                        checkWifi = 5;
//                        checkWifiConnect();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI", getString(R.string.un_connected)));
                                binding.wifiIPTv.setText(String.format(getString(R.string.status_ip), "------"));

                                binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
                                binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                binding.clConnectNetwork.setVisibility(View.VISIBLE);
                                binding.clConnectIP.setVisibility(View.INVISIBLE);

                            }
                        });
                    }

                }

                @Override
                public void onReturnString(String s) throws RemoteException {

                }
            });



        } else {
            updateUi();

        }

    }


    public void updateUi() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi), "WIFI"
                        , getString(R.string.connected)));
                binding.wifiIPTv.setText(String.format(getString(R.string.status_ip)
                        , MainActivity.ipConnect));
                binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
                binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                binding.clConnectNetwork.setVisibility(View.INVISIBLE);
                binding.clConnectIP.setVisibility(View.VISIBLE);
                Log.d(TAG, "   " + binding.clConnectIP.getVisibility() + "   " + binding.clConnectNetwork.getVisibility());
            }
        });
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
        this.list.clear();
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
    public void onDestroyView() {
        if (wifiScanner != null) {
            wifiScanner.release();
        }
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

        listView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mList));

        // 列表项点击事件
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            //Toast.makeText(context, "点击了: " + mList.get(position), Toast.LENGTH_SHORT).show();
            if (mList != null && mList.size() > position) {
                binding.ssidSpinner.setText(mList.get(position));
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
