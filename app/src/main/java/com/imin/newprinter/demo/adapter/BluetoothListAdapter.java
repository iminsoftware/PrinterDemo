package com.imin.newprinter.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.bean.BluetoothBean;

import java.util.List;

/**
 * 蓝牙列表
 */
public class BluetoothListAdapter extends BaseAdapter {
    private static final int CONTENT = 1;
    private static final int TITLE = 0;
    private Context mContext;
    private List<BluetoothBean> newDevices;
    private List<BluetoothBean> pairedDevices;

    public BluetoothListAdapter(List<BluetoothBean> pairedDevices, List<BluetoothBean> newDevices, Context context) {
        this.pairedDevices = pairedDevices;
        this.newDevices = newDevices;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return this.pairedDevices.size() + this.newDevices.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return Integer.valueOf(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case 0:
                View convertView2 = LayoutInflater.from(this.mContext).inflate(R.layout.widget_list_text_item, parent, false);
                TextView tv_title = (TextView) convertView2.findViewById(R.id.text);
                tv_title.setTextColor(this.mContext.getResources().getColor(R.color.color_01CA86));
                tv_title.setGravity(3);
                if (position == 0) {
                    tv_title.setText(this.mContext.getResources().getString(R.string.paired));
                    return convertView2;
                }
                tv_title.setText(this.mContext.getResources().getString(R.string.unpaired));
                return convertView2;
            case 1:
                BluetoothBean bluetoothParameter = null;
                if (position < this.pairedDevices.size() + 1) {
                    BluetoothBean bluetoothParameter2 = this.pairedDevices.get(position - 1);
                    bluetoothParameter = bluetoothParameter2;
                }
                if (position > this.pairedDevices.size() + 1 && this.newDevices.size() > 0) {
                    BluetoothBean bluetoothParameter3 = this.newDevices.get((position - this.pairedDevices.size()) - 2);
                    bluetoothParameter = bluetoothParameter3;
                }
                if (bluetoothParameter != null) {
                    View convertView3 = LayoutInflater.from(this.mContext).inflate(R.layout.bluetooth_list_item, parent, false);
                    TextView tvName = (TextView) convertView3.findViewById(R.id.b_name);
                    TextView tvMac = (TextView) convertView3.findViewById(R.id.b_mac);
                    TextView tvStrength = (TextView) convertView3.findViewById(R.id.b_info);
                    tvName.setText(bluetoothParameter.getBluetoothName());
                    tvMac.setText(bluetoothParameter.getBluetoothMac());
                    tvStrength.setText(bluetoothParameter.getBluetoothStrength());
                    return convertView3;
                }
                return convertView;
            default:
                return convertView;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == this.pairedDevices.size() + 1 || position == 0) ? 0 : 1;
    }
}
