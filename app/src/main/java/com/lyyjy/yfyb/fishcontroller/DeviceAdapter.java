package com.lyyjy.yfyb.fishcontroller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/9/17.
 */
public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    private int mResourceId;

    public DeviceAdapter(Context context, int resource, List<BluetoothDevice> objects) {
        super(context, resource, objects);

        mResourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
        TextView deviceName = (TextView) view.findViewById(R.id.tvDevice);
        deviceName.setText(device.getName()+"\n"+device.getAddress());
        return view;
    }
}
