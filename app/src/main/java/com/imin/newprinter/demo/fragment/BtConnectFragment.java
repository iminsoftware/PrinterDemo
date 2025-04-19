package com.imin.newprinter.demo.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.BluetoothListAdapter;
import com.imin.newprinter.demo.bean.BluetoothBean;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentBtConnectBinding;

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
public class BtConnectFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = BtConnectFragment.class.getSimpleName();

    private com.imin.newprinter.demo.databinding.FragmentBtConnectBinding binding;
    private int OPEN_BLUETOOTH_REQUEST_CODE = 0x00;
    private BluetoothAdapter mBluetoothAdapter;
    private LocationManager manager;
    //已配对列表
    private List<BluetoothBean> pairedDevices = new ArrayList<>();
    //新设备列表
    private List<BluetoothBean> newDevices = new ArrayList<>();
    private Set<BluetoothDevice> boundDevices = new HashSet<>();
    private BluetoothListAdapter adapter;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.d(TAG, "onReceive: Search finish");
                    return;
                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int bluetooth_state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, Integer.MIN_VALUE);
                    if (bluetooth_state == BluetoothAdapter.STATE_TURNING_OFF) {//蓝牙关闭
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
                return;
            }
            updateBlueToothSignal(bluetoothBean);
        }
    };
    /**
     * 获取本机已配对列表
     */
    private void getBoundDevices(){
        boundDevices= mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : boundDevices) {
            BluetoothBean deviceInfo = new BluetoothBean();
            deviceInfo.setBluetoothName(device.getName());
            deviceInfo.setBluetoothMac(device.getAddress());
            pairedDevices.add(deviceInfo);//添加到已匹配的集合

        }
        adapter.notifyDataSetChanged();
    }
    /**
     * 添加新蓝牙设备
     *
     * @param bluetoothBean
     */
    private void addNewBlueTooth(BluetoothBean bluetoothBean) {
        for (BluetoothBean p : newDevices) {
            if (p.getBluetoothMac().equals(bluetoothBean.getBluetoothMac())) {//防止重复添加
                return;
            }
        }
        newDevices.add(bluetoothBean);
        Collections.sort(newDevices, new Signal());
        adapter.notifyDataSetChanged();
    }

    /**
     * 更新蓝牙信号
     *
     * @param bluetoothBean
     */
    private void updateBlueToothSignal(BluetoothBean bluetoothBean) {
        for (int i = 0; i < pairedDevices.size(); i++) {
            if (pairedDevices.get(i).getBluetoothMac().equals(bluetoothBean.getBluetoothMac())) {
                pairedDevices.get(i).setBluetoothStrength(bluetoothBean.getBluetoothStrength());
                adapter.notifyDataSetChanged();
                return;
            }
        }
        pairedDevices.add(bluetoothBean);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0 || position == pairedDevices.size() + 1) {
            return;
        }
        String mac = null;
        String name = null;
        if (position <= pairedDevices.size()) {//点击已配对设备列表
            mac = pairedDevices.get(position - 1).getBluetoothMac();
            name = pairedDevices.get(position - 1).getBluetoothName();
        } else {//点击新设备列表
            mac = newDevices.get(position - 2 - pairedDevices.size()).getBluetoothMac();
            name = newDevices.get(position - 2 - pairedDevices.size()).getBluetoothName();
        }
        Toast.makeText(getContext(), name + mac, Toast.LENGTH_SHORT).show();
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
        initView();
        initData();
        return binding.getRoot();
    }

    private void initView() {
        binding.searchBt.setOnClickListener(this);
        adapter = new BluetoothListAdapter(pairedDevices, newDevices, getContext());
        binding.lvBluetoothDevice.setAdapter(adapter);
        binding.lvBluetoothDevice.setOnItemClickListener(this);
    }

    private void initData() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Don't support BlueTooth");
            return;
        } else {
            registerBlueToothBroadcast();
            //蓝牙未打开
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, OPEN_BLUETOOTH_REQUEST_CODE);
            } else {//蓝牙已打开
                binding.searchBt.performClick();
//                getBoundDevices();
            }
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
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_bt:
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, OPEN_BLUETOOTH_REQUEST_CODE);
                    return;
                }
                binding.srlRefresh.setRefreshing(true);
                binding.searchBt.setVisibility(View.GONE);
                searchBlueTooth();
                break;
        }
    }

    /**
     * 搜索设备
     */
    public synchronized void searchBlueTooth() {
        mBluetoothAdapter.startDiscovery();//开始搜索
    }
}
