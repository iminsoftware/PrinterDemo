package com.imin.newprinter.demo.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.feature.tui.util.ToastUtil;
import com.imin.newprinter.demo.MainActivity;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.BluetoothListAdapter;
import com.imin.newprinter.demo.bean.BluetoothBean;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentBtConnectBinding;
import com.imin.newprinter.demo.utils.LoadingDialogUtil;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.printer.IWirelessPrintResult;
import com.imin.printer.PrinterHelper;
import com.imin.printer.enums.ConnectType;
import com.imin.printer.enums.WirelessConfig;
import com.imin.printer.wireless.WirelessPrintStyle;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: hy
 * @date: 2025/4/18
 * @description:
 */
@SuppressLint("MissingPermission")
public class BtConnectFragment extends BaseFragment {
    private static final String TAG = "PrintDemo_BtConnectFragment";

    private com.imin.newprinter.demo.databinding.FragmentBtConnectBinding binding;
    private int OPEN_BLUETOOTH_REQUEST_CODE = 0x00;
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager manager;
    //已配对列表
    private List<BluetoothBean> pairedDevices = new ArrayList<>();
    //新设备列表
    private List<BluetoothBean> newDevices = new ArrayList<>();
    private Set<BluetoothDevice> boundDevices = new HashSet<>();
    private BaseQuickAdapter adapter;
    private BaseQuickAdapter adapter2;
    //    private BluetoothListAdapter adapter,adapter2;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser + "    " + isResumed());
        if (isVisibleToUser && isResumed()) {
            // 当 Fragment 对用户可见时执行操作（兼容旧版本）
            initData();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {//搜索完成
                    Log.d(TAG, "onReceive: Search finish");
                    ToastUtil.showShort(context, getString(R.string.search_finish));
                    binding.srlRefresh.finishRefresh();
                    adapter.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                    return;
                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int bluetooth_state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, Integer.MIN_VALUE);
                    if (bluetooth_state == BluetoothAdapter.STATE_TURNING_OFF) {//蓝牙关闭
                        binding.srlRefresh.finishRefresh();
                        Log.e(TAG, "BlueTooth Turn Off\n");
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");

            if (device.getBluetoothClass().getMajorDeviceClass() != 1536) {//只显示蓝牙打印机
                return;
            }
            if (Utils.isEmpty(device.getName())) {
                return;
            }
            if (!device.getName().contains("80mm Wireless")) {
                return;
            }
            BluetoothBean bluetoothBean = new BluetoothBean();
            int rssi = intent.getExtras().getShort("android.bluetooth.device.extra.RSSI");
            if (device != null && device.getName() != null) {
                bluetoothBean.setBluetoothName(device.getName());
            } else {
                bluetoothBean.setBluetoothName("unKnow");
            }
            bluetoothBean.setBluetoothMac(device.getAddress());
            bluetoothBean.setBluetoothStrength(rssi + "");
            Log.d(TAG, "onReceive: \nBlueToothName:\t" + device.getName() + "\nMacAddress:\t" + device.getAddress() + "\nrssi:\t" + rssi);
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//未配对
                addNewBlueTooth(bluetoothBean);

            }else {
                updateBlueToothSignal(bluetoothBean);
            }

        }
    };

    /**
     * 获取本机已配对列表
     */
    private void getBoundDevices() {
        boundDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : boundDevices) {
            if (!device.getName().contains("80mm Wireless Printer")) {
                continue;
            }
            BluetoothBean deviceInfo = new BluetoothBean();
            deviceInfo.setBluetoothName(device.getName());
            deviceInfo.setBluetoothMac(device.getAddress());
            pairedDevices.add(deviceInfo);//添加到已匹配的集合

        }

        adapter.replaceData(pairedDevices);
    }

    /**
     * 添加新蓝牙设备
     *
     * @param bluetoothBean
     */
    private void addNewBlueTooth(BluetoothBean bluetoothBean) {
        boolean isAdd = true;
        for (BluetoothBean p : newDevices) {
            if (p.getBluetoothMac().equals(bluetoothBean.getBluetoothMac())) {//防止重复添加
                isAdd = false;
            }
        }
        if (isAdd){
            newDevices.add(bluetoothBean);
        }
        Collections.sort(newDevices, new Signal());
        adapter2.replaceData(newDevices);


    }

    /**
     * 更新蓝牙信号
     *
     * @param bluetoothBean
     */
    private void updateBlueToothSignal(BluetoothBean bluetoothBean) {
        Log.d(TAG, "updateBlueToothSignal=>");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isContain = true;
                for (int i = 0; i < pairedDevices.size(); i++) {
                    if (pairedDevices.get(i).getBluetoothMac().equals(bluetoothBean.getBluetoothMac())) {
                        pairedDevices.get(i).setBluetoothStrength(bluetoothBean.getBluetoothStrength());
                        isContain = false;
                    }
                }

                if (isContain){
                    pairedDevices.add(bluetoothBean);
                }

                adapter.replaceData(pairedDevices);
                Log.d(TAG, "updateBlueToothSignal=>"+adapter.getData().size()+"  i "+isContain);
            }
        });

    }




    // 自定义比较器：按信号强度排序
    static class Signal implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            BluetoothBean p1 = (BluetoothBean) object1; // 强制转换
            BluetoothBean p2 = (BluetoothBean) object2;
            return p1.getBluetoothStrength().compareTo(p2.getBluetoothStrength());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBtConnectBinding.inflate(inflater);
        registerBlueToothBroadcast();
        initView();
        return binding.getRoot();
    }


    private void initView() {
        binding.srlRefresh.autoRefresh();
        binding.srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                searchBtData();

//                binding.srlRefresh.finishRefresh(3000);
            }
        });


        binding.searchBt.setOnClickListener(view -> {
            searchBtData();

        });

        adapter = new BaseQuickAdapter<BluetoothBean, BaseViewHolder>(R.layout.bluetooth_list_item) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, BluetoothBean functionBean) {
                Log.d(TAG, "convert: functionBean= " + functionBean);
                if (functionBean != null) {
                    TextView tvName = (TextView) viewHolder.getView(R.id.b_name);
                    TextView tvMac = (TextView) viewHolder.getView(R.id.b_mac);
                    TextView tvStrength = (TextView) viewHolder.getView(R.id.b_info);
                    tvName.setText(functionBean.getBluetoothName());
                    tvMac.setText(functionBean.getBluetoothMac());
                    tvStrength.setText(functionBean.getBluetoothStrength());

                }
            }
        };

        adapter2 = new BaseQuickAdapter<BluetoothBean, BaseViewHolder>(R.layout.bluetooth_list_item) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, BluetoothBean functionBean) {
                Log.d(TAG, "convert: functionBean= " + functionBean);
                if (functionBean != null) {
                    TextView tvName = (TextView) viewHolder.getView(R.id.b_name);
                    TextView tvMac = (TextView) viewHolder.getView(R.id.b_mac);
                    TextView tvStrength = (TextView) viewHolder.getView(R.id.b_info);
                    tvName.setText(functionBean.getBluetoothName());
                    tvMac.setText(functionBean.getBluetoothMac());
                    tvStrength.setText(functionBean.getBluetoothStrength());

                }
            }
        };


//        adapter = new BluetoothListAdapter(pairedDevices, getContext());
//        adapter2 = new BluetoothListAdapter(newDevices,getContext());
        binding.lvBluetoothDevice.setAdapter(adapter);
        adapter.setNewInstance(pairedDevices);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

                if (pairedDevices.size() >0 && position<= pairedDevices.size()-1){
                    if (mBluetoothAdapter != null){
                        mBluetoothAdapter.cancelDiscovery();
                    }

                    String mac = pairedDevices.get(position).getBluetoothMac();
                    String name = pairedDevices.get(position).getBluetoothName();


                    connectBt(mac,name);
                }

            }
        });
//        binding.lvBluetoothDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                if (position == 0 || position == pairedDevices.size() + 1) {
//                    return;
//                }
//                if (mBluetoothAdapter != null){
//                    mBluetoothAdapter.cancelDiscovery();
//                }
//
//               String mac = pairedDevices.get(position).getBluetoothMac();
//               String name = pairedDevices.get(position).getBluetoothName();
//
////                if (position <= pairedDevices.size()) {//点击已配对设备列表
////                    mac = pairedDevices.get(position - 1).getBluetoothMac();
////                    name = pairedDevices.get(position - 1).getBluetoothName();
////                } else {//点击新设备列表
////                    mac = newDevices.get(position - 2 - pairedDevices.size()).getBluetoothMac();
////                    name = newDevices.get(position - 2 - pairedDevices.size()).getBluetoothName();
////                }
//
//                connectBt(mac,name);
//
//            }
//        });

        binding.unPairedDeviceLv.setAdapter(adapter2);
        adapter2.setNewInstance(newDevices);
        adapter2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (newDevices.size()>0 && position<= newDevices.size()){
                    if (mBluetoothAdapter != null){
                        mBluetoothAdapter.cancelDiscovery();
                    }

                    String mac = newDevices.get(position).getBluetoothMac();
                    String name = newDevices.get(position).getBluetoothName();
                    connectBt(mac,name);
                }

            }
        });
//        binding.unPairedDeviceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                if (position == 0 || position == pairedDevices.size() + 1) {
//                    return;
//                }
//                if (mBluetoothAdapter != null){
//                    mBluetoothAdapter.cancelDiscovery();
//                }
//
//                String mac = newDevices.get(position).getBluetoothMac();
//                String name = newDevices.get(position).getBluetoothName();
//            }
//        });

        binding.viewTitle.setRightCallback(v -> {
            Log.d(TAG, "setting: ");
            switchFragment(100);
        });
        binding.btDisconnect.setOnClickListener(view -> {
            PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                    .setWirelessStyle(WirelessConfig.DISCONNECT_BT), new IWirelessPrintResult.Stub() {
                @Override
                public void onResult(int i, String s) throws RemoteException {
                    binding.btStatusTv.setText(String.format(getString(R.string.status_wifi), "BT"
                            , getString(R.string.un_connected)));
                    MainActivity.btContent = "";

                }

                @Override
                public void onReturnString(String s) throws RemoteException {

                }
            });
        });

    }

    private void connectBt(String mac ,String name) {
        LoadingDialogUtil.getInstance().show(getContext(), "");

        MainActivity.connectAddress = mac;
        MainActivity.btContent = mac;
        Toast.makeText(getContext(), name + mac, Toast.LENGTH_SHORT).show();

        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                .setConfig(ConnectType.BT.getTypeName()), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {

            }

            @Override
            public void onReturnString(String s) throws RemoteException {

            }
        });

        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                .setWirelessStyle(WirelessConfig.BT_CONNECT_ADDR)
                .setConfig(MainActivity.btContent), new IWirelessPrintResult.Stub() {
            @Override
            public void onResult(int i, String s) throws RemoteException {
                Log.d(TAG, "WIFI_CONNECT=>" + s + "  i=" + i);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (i == 0) {
                            MainActivity.connectType = "BT";
                            MainActivity.connectContent = name + "\t" + mac;
                            switchFragment(4);
                        } else {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                    getText(R.string.connect_fail), Toast.LENGTH_LONG).show());
                        }
                        LoadingDialogUtil.getInstance().hide();
                    }
                });

            }

            @Override
            public void onReturnString(String s) throws RemoteException {
                Log.d(TAG, "WIFI_CONNECT =>" + s);
            }
        });

    }

    private void initData() {


        if (Utils.isEmpty(MainActivity.btContent)) {
            binding.btStatusTv.setText(String.format(getString(R.string.status_wifi), "BT"
                    , getString(R.string.un_connected)));
        } else {
            binding.btStatusTv.setText(String.format(getString(R.string.status_wifi), "BT"
                    , getString(R.string.connected)));
        }

        Log.e(TAG, "Don't support BlueTooth" + binding.btStatusTv.getText());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Don't support BlueTooth");
            return;
        } else {

            //蓝牙未打开
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, OPEN_BLUETOOTH_REQUEST_CODE);
            } else {//蓝牙已打开
                if (checkBluetoothPermissions()) {
                    searchBtData();
                }

//                getBoundDevices();
            }
        }
    }

    private void searchBtData(){
        if (mBluetoothAdapter != null){
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, OPEN_BLUETOOTH_REQUEST_CODE);
                return;
            }


            searchBlueTooth();
        }
    }

    /**
     * 注册广播
     */
    private void registerBlueToothBroadcast() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);//搜索蓝牙设备
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//蓝牙搜索完成
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态改变
            getActivity().registerReceiver(broadcastReceiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "register bluetooth error" + e.getMessage());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

            if (mBluetoothAdapter != null) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter = null;
            }
            pairedDevices.clear();
            pairedDevices = null;
            newDevices.clear();
            newDevices = null;
            boundDevices = null;
            manager = null;
            if (broadcastReceiver != null) {
                getActivity().unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 检查蓝牙基础权限（适配 Android 12+）
    public boolean checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
        }
    }


    /**
     * 搜索设备
     */
    @SuppressLint("MissingPermission")
    public synchronized void searchBlueTooth() {
        binding.srlRefresh.autoRefresh();
        if (!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();//开始搜索
        }
    }
}
