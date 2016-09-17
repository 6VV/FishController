package com.lyyjy.yfyb.fishcontroller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

/**
 * Created by Administrator on 2016/9/17.
 */
public class BluetoothHelper {
    private static final String TAG = "BluetoothHelper";

    private enum ConnectState{
        CONNECTED,
        CONNECTING,
        DISCONNECT,
    }
    private static BluetoothHelper mInstance=null;
    private ConnectState mConnectState;

    public static BluetoothHelper getInstance(){
        if (mInstance==null){
            mInstance=new BluetoothHelper();
        }

        return mInstance;
    }

    private Context mContext;

    /*服务及特性UUID*/
    private static final UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");   //读写对应的服务UUID
    private static final UUID UUID_CHARACTERISTIC_WRITE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");  //写入特性对应的UUID
    private static final UUID UUID_CHARACTERISTIC_READ = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");   //读取特性对应的UUID

    //蓝牙相关
    private BluetoothDevice mDeviceConnected=null;
    private BluetoothAdapter mBluetoothAdapter=null;   //蓝牙适配器
    private BluetoothGatt mBluetoothGatt=null;

    private boolean mIsScanning=false;

    private BluetoothGattCharacteristic mBluetoothGattCharacteristicWrite;  //写入用Characteristic
    private MyLeScanCallback mLeScanCallback;
    private IBluetoothCallback mIBluetoothCallback=null;

    private BluetoothHelper(){
        mContext=ContextUtil.getInstance();
        getAdapter();
    }

    public void registCallback(IBluetoothCallback iBluetoothCallback){
        mIBluetoothCallback=iBluetoothCallback;
    }

    public void search(boolean enable){
        if (!mBluetoothAdapter.isEnabled()){
            return;
        }
        if (enable) {
            if (mIsScanning){
                return;
            }
            if (mIBluetoothCallback==null){
                return;
            }
            mLeScanCallback=new MyLeScanCallback(mIBluetoothCallback);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mIsScanning = true;
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mIsScanning = false;
        }
    }


    private void getAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (isBluetoothInvalid()
                || !mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定",null);

            if (isBluetoothInvalid()) {
                dialog.setTitle("不支持蓝牙设备");
            }

            if (!mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                dialog.setTitle("不支持蓝牙4.0");
            }

            dialog.show();
        }
    }

    private boolean isBluetoothInvalid(){
        return mBluetoothAdapter==null;
    }

    public void connect(BluetoothDevice device) {

        if (device==null){
            Toast.makeText(ContextUtil.getInstance(),"未找到该设备",Toast.LENGTH_SHORT).show();
            return;
        }

        mDeviceConnected=device;

        if(!mBluetoothAdapter.isEnabled()){
            Toast.makeText(ContextUtil.getInstance(), "请先开启蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        //销毁原有gatt
        disconnect();
        mConnectState =ConnectState.CONNECTING;
        mBluetoothGatt = mDeviceConnected.connectGatt(mContext, false, new MyBluetoothGattCallback());
        Log.e(TAG,"connect");
    }

    public void send(byte[] data) {
        if (mBluetoothGatt == null) {
            return ;
        }

        if (mConnectState==ConnectState.DISCONNECT){
            return  ;
        }

        mBluetoothGattCharacteristicWrite.setValue(data);
        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristicWrite);
    }

    public void destroy(){
        disconnect();
    }

    private void disconnect() {
        mConnectState = ConnectState.DISCONNECT;

        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    private class MyLeScanCallback implements BluetoothAdapter.LeScanCallback{
        private IBluetoothCallback mIRemoteScan;

        public MyLeScanCallback(IBluetoothCallback iRemoteScan){
            mIRemoteScan=iRemoteScan;
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mIRemoteScan.onSearch(device);
        }
    }

    private class MyBluetoothGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectState=ConnectState.CONNECTED;
                mBluetoothGatt.discoverServices();
                Log.e(TAG,"connected");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            checkServiceAndConnect();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    }

    private void checkServiceAndConnect() {
        BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(UUID_SERVICE);    //获取特定服务

        //若未找到相关服务，则断开连接
        if (bluetoothGattService == null) {
            Toast.makeText(mContext,"未找到相关服务",Toast.LENGTH_LONG).show();
            disconnect();
            return;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristicRead = bluetoothGattService.getCharacteristic(UUID_CHARACTERISTIC_READ);    //获取读特性
        mBluetoothGattCharacteristicWrite=bluetoothGattService.getCharacteristic(UUID_CHARACTERISTIC_WRITE);    //获取写特性

        //若未找到相关特性
        if (bluetoothGattCharacteristicRead == null||mBluetoothGattCharacteristicWrite==null) {
            Toast.makeText(mContext,"未找到相关特性",Toast.LENGTH_LONG).show();
            disconnect();
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristicRead, true);   //监听读事件

        mIBluetoothCallback.onConnectChanged();

        Log.e(TAG,"onConnectChanged");

    }
}
