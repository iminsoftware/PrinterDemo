package com.imin.newprinter.demo.fragment;

import android.annotation.SuppressLint;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
public class WifiConnectFragment extends BaseFragment {
    private static final String TAG = "PrintDemo_WifiConnectFragment";
    private com.imin.newprinter.demo.databinding.FragmentWifiConnectBinding binding;
    ArrayList<String> list = new ArrayList<>();


    private static final String ARG_WIFI_LIST = "wifiList";

    public static WifiConnectFragment newInstance(ArrayList<String> wifiList) {
        WifiConnectFragment fragment = new WifiConnectFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_WIFI_LIST, wifiList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            list = getArguments().getStringArrayList(ARG_WIFI_LIST);
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWifiConnectBinding.inflate(inflater);

        return binding.getRoot();
    }

    boolean isOpenEasy = false;
    boolean outoConnect = true;//自动连接   手动输入ip连接

    private ArrayAdapter<String> adapter;

    @Override
    public void onResume() {
        super.onResume();
        initView();
        initData();
        if (Utils.isEmpty(MainActivity.connectType)){
            binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi),"WIFI"
                    ,getString(R.string.un_normal)));
            binding.wifiIPTv.setText(String.format(getString(R.string.status_ip)
                    ,"-------"));
        }else {
            if (MainActivity.connectType.equals("WIFI")){
                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi),"WIFI"
                        ,getString(R.string.normal)));
                binding.wifiIPTv.setText(String.format(getString(R.string.status_ip)
                        ,MainActivity.connectAddress));

            }else {
                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi),"WIFI"
                        ,getString(R.string.un_normal)));

                binding.wifiIPTv.setText(String.format(getString(R.string.status_ip)
                        ,"-------"));
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        Log.d(TAG, "加载页面: "+list.size());
        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ssidSpinner.setAdapter(adapter);
        if (list != null && list.size() >0){
            setSpinnerData(list);
        }
        binding.autoConnectIv.setImageResource(R.drawable.ic_check);
        binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_open);
        // 切换为密码隐藏（星号显示）
        binding.pwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

        binding.ipEt.setEnabled(false);



        binding.connectNetworkTv.setOnClickListener(view -> {
            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
            binding.clConnectNetwork.setVisibility(View.VISIBLE);
            binding.clConnectIP.setVisibility(View.GONE);
        });

        binding.connectTv.setOnClickListener(v->{

            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
            binding.clConnectNetwork.setVisibility(View.GONE);
            binding.clConnectIP.setVisibility(View.VISIBLE);
            binding.clConnectIP.requestLayout();
            Log.d(TAG, "加载页面: "+binding.clConnectIP.getVisibility());
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
            if (isOpenEasy){
                binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_close);
                // 切换为明文显示
                binding.pwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.pwdEt.setSelection(binding.pwdEt.getText().length());

            }else {
                binding.pwtOpenIv.setImageResource(R.drawable.ic_pwd_open);
                // 切换为密码隐藏（星号显示）
                binding.pwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.pwdEt.setSelection(binding.pwdEt.getText().length());
            }

        });

        binding.swichStatic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    binding.clStatic.setVisibility(View.GONE);
                }else {
                    binding.clStatic.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.networkConfirmTv.setOnClickListener(view -> {
            if (binding.ssidSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
                String selectedWifi = (String) binding.ssidSpinner.getSelectedItem();
                // 解析SSID和BSSID
                if (selectedWifi.contains(" (")) {
                    String[] parts = selectedWifi.split(" \\(");
                    String ssid = parts[0];
                    String bssid = parts[1].replace(")", "");
                    String pwd = binding.pwdEt.getText().toString().trim();
                    if (!bssid.equals("OPEN")){
                        if (Utils.isEmpty(pwd)){
                            Toast.makeText(getContext(), getString(R.string.toast1), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Log.d(TAG,"WIFI_IP_TYPE ssid=>"+ssid+"  pwd="+pwd);
                    if (binding.swichStatic.isChecked()){
                        connectToWifi(ssid,pwd);
                    }else {
                        conectStaticWifi(ssid,pwd);
                    }

//                    showConnectionDialog(ssid, bssid);
                }
            } else {
                Toast.makeText(getContext(), "请先选择WiFi网络", Toast.LENGTH_SHORT).show();
            }
        });

        binding.ipEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.ipConfirmTv.setOnClickListener(v->{
            if (!outoConnect){
                String ip = binding.ipEt.getText().toString().trim();
                if(Utils.isEmpty(ip)){
                    Toast.makeText(getContext(), "请先选择正确的ip", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!NetworkValidator.validateIP(ip)){
                    Toast.makeText(getContext(), "请先选择正确的ip", Toast.LENGTH_SHORT).show();
                    return;
                }
                MainActivity.connectAddress = ip;
            }

            PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                    .setConnectType(ConnectType.WIFI.getTypeName())
                    .setConfig(MainActivity.connectAddress), new IWirelessPrintResult.Stub() {
                @Override
                public void onResult(int i, String s) throws RemoteException {
                    Log.d(TAG,"WIFI_CONNECT=>"+s+"  i="+i);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (i == 0){
                                MainActivity.connectType = "WIFI";
                                MainActivity.connectContent = binding.wifiIPTv.getText().toString().trim();
                                switchFragment(4);
                            }
                        }
                    });

                }

                @Override
                public void onReturnString(String s) throws RemoteException {
                    Log.d(TAG,"WIFI_CONNECT =>"+s);
                }
            });




        });

        binding.viewTitle.setRightCallback(v -> {
            Log.d(TAG, "setting: ");
            switchFragment(100);
        });

    }

    //连接静态ip
    private void conectStaticWifi(String ssid, String pwd) {
        //设置当前动态还是静态
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP_TYPE).setConfig(binding.swichStatic.isChecked() ? IpType.DHCP.getTypeName() : IpType.STATIC.getTypeName()), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG,"WIFI_IP_TYPE =>"+s+"  i="+i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }
        });
        //配网
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIRELESS_NET_SETUP).setSsid(ssid).setPwd(pwd), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG,"配网=>"+s+"  i="+i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }


        });

        String ip = binding.staticIpEt.getText().toString().trim();
        String mask = binding.maskEt.getText().toString().trim();
        String gw = binding.gwEt.getText().toString().trim();
        String dns = binding.dnsEt.getText().toString().trim();
        if (Utils.isEmpty(ip) || Utils.isEmpty(mask) || Utils.isEmpty(gw) || Utils.isEmpty(dns)){
            Toast.makeText(getActivity(), "网关与 IP 不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkValidator.validateGateway(ip, gw, mask)) {
            Toast.makeText(getActivity(), "网关与 IP 不在同一子网", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkValidator.validateDNS(dns)){
            Toast.makeText(getActivity(), "格式错误", Toast.LENGTH_SHORT).show();
            return;
        }

        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP).setConfig(ip), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG,"set ip=>"+s+"  i="+i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {
                binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi),"WIFI",s));
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
                Log.d(TAG,"gw=>"+s+"  i="+i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }


        });
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_DNS).setConfig(dns), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG,"dns=>"+s+"  i="+i);
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
                        if (s != null){
                            binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
                            binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                            binding.clConnectNetwork.setVisibility(View.GONE);
                            binding.clConnectIP.setVisibility(View.VISIBLE);

                            binding.wifiIPTv.setText(String.format(getString(R.string.status_ip),s));
                        }
                    }
                });

    }

    private void connectToWifi(String ssid, String pwd) {
        // 这里需要实现具体的WiFi连接逻辑
        Log.d(TAG, "正在连接: " + ssid);
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle().setWirelessStyle(WirelessConfig.WIFI_IP_TYPE).setConfig(binding.swichStatic.isChecked() ? IpType.DHCP.getTypeName() : IpType.STATIC.getTypeName()), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG,"WIFI_IP_TYPE =>"+s+"  i="+i);
            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }
        });
        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                        .setWirelessStyle(WirelessConfig.WIRELESS_NET_SETUP)
                .setSsid(ssid)
                .setPwd(pwd), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG, "配网回调==: i= " + i+" ,s=>"+s);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialogUtil.getInstance().show(getContext(),getString(R.string.loading));
                    }
                });


                if (i == 0){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi),"WIFI",getString(R.string.normal)));
                        }
                    });

                    PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                                    .setWirelessStyle(WirelessConfig.WIFI_CONNECT_STATUS)
                            , new Stub() {
                                @Override
                                public void onResult(int i, String s) throws RemoteException {
                                    Log.d(TAG, "WIFI_CONNECT_STATUS==: i= " + i+" ,s=>"+s);

                                }

                                @Override
                                public void onReturnString(String s) throws RemoteException {
                                    Log.d(TAG, "WIFI_CONNECT_STATUS==: s= " + s);
                                    PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                                                    .setWirelessStyle(WirelessConfig.WIFI_IP)
                                            , new Stub() {
                                                @Override
                                                public void onResult(int i, String s) throws RemoteException {
                                                    Log.d(TAG, "请求回去==: i= " + i+" ,s=>"+s);

                                                }

                                                @Override
                                                public void onReturnString(String s) throws RemoteException {
                                                    Log.d(TAG, "ip回调==: s= " + s);

                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (s != null){
                                                                LoadingDialogUtil.getInstance().hide();
                                                                MainActivity.connectAddress = s;
                                                                binding.connectTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_green_corner_5));
                                                                binding.connectNetworkTv.setBackground(getContext().getResources().getDrawable(R.drawable.dra_gray_corner_5));
                                                                binding.clConnectNetwork.setVisibility(View.GONE);
                                                                binding.clConnectIP.setVisibility(View.VISIBLE);

                                                                binding.wifiIPTv.setText(String.format(getString(R.string.status_ip),s));
                                                            }
                                                        }
                                                    });

                                                }
                                            });

                                }
                            });


                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi),"WIFI",getString(R.string.un_normal)));
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

    private void initData() {

//        PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
//                        .setWirelessStyle(WirelessConfig.WIFI_CONNECT_STATUS)
//                , new IWirelessPrintResult.Stub() {
//                    @Override
//                    public void onResult(int i, String s) throws RemoteException {
//                        Log.d(TAG, "WIFI_CONNECT_STATUS==: i= " + i+" ,s=>"+s);
//
//                    }
//
//                    @Override
//                    public void onReturnString(String s) throws RemoteException {
//                        Log.d(TAG, "WIFI_CONNECT_STATUS==: s= " + s);//1已连接  0是没连接
//                        if (!Utils.isEmpty(s) && s.equals("0")){
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi),"WIFI",getString(R.string.normal)));
//                                    binding.wifiIPTv.setText(String.format(getString(R.string.status_ip),MainActivity.connectAddress));
//                                }
//                            });
//                        }else {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    binding.wifiStatusTv.setText(String.format(getString(R.string.status_wifi),"WIFI",getString(R.string.un_normal)));
//                                }
//                            });
//                        }
//
//                    }
//                });


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

    public void setSpinnerData(List<String> wifiList){
        Log.d(TAG, "setSpinnerData=  " + wifiList);
        if (adapter != null){
            if (adapter.getCount() == 0){
                adapter.clear();
                adapter.addAll(wifiList);
                adapter.notifyDataSetChanged();
                binding.ssidSpinner.setAdapter(adapter);
            }

        }
        binding.ssidSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String s = adapter.getItem(i);
                Log.d(TAG, "加载页面: "+s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
