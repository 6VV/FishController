package com.lyyjy.yfyb.fishcontroller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements IBluetoothCallback{

    private static final String TAG = "SearchActivity";
    private BluetoothHelper mBluetoothHelper;

    private ListView mListViewDevices;
    private List<BluetoothDevice> mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mBluetoothHelper=BluetoothHelper.getInstance();
        mDeviceList=new ArrayList<>();
        mListViewDevices = (ListView) findViewById(R.id.listViewDevices);
        mListViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDeviceList==null || mDeviceList.size()<=position){
                    Toast.makeText(SearchActivity.this, "未找到该设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                final BluetoothDevice device = mDeviceList.get(position);
                if (device == null) {
                    Toast.makeText(SearchActivity.this, "未找到该设备", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
                alertDialog.setTitle("是否连接该设备");
                alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBluetoothHelper.connect(device);
                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
            }break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBluetoothHelper.registCallback(this);
        mDeviceList.clear();
        mBluetoothHelper.search(true);
    }

    @Override
    public void onSearch(BluetoothDevice device) {

        String name=device.getName();

        byte[] byteName = name.getBytes();

        if (byteName.length<2){
            return;
        }

        if (byteName[0] != 0x01 || byteName[1] != 0x02){
            return;
        }

        boolean isFind=false;
        for (BluetoothDevice bluetoothDevice:mDeviceList){
            if (bluetoothDevice.getAddress().equals(device.getAddress())){
                isFind=true;
                break;
            }
        }

        if (isFind){
            return;
        }

        mDeviceList.add(device);

        mHandler.sendEmptyMessage(0);
    }

    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mListViewDevices.setAdapter(new DeviceAdapter(SearchActivity.this,R.layout.layout_device_item,mDeviceList));
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothHelper.search(false);
    }

    @Override
    public void onConnectChanged() {
        finish();
    }
}
