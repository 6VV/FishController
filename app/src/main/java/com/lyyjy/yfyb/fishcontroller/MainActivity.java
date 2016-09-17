package com.lyyjy.yfyb.fishcontroller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements IBluetoothCallback, View.OnTouchListener {

    private static final String TAG = "MainActivity";

    private Button mBtnFishUp;
    private Button mBtnFishLeft;
    private Button mBtnFishRight;

    private BluetoothHelper mBluetoothHelper;
    private MessageSender mMessageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });

        findViewById(R.id.btnAutoSwim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendSwimMode(DeviceController.CommandCode.AUTO_SWIM);
            }
        });

        findViewById(R.id.btnMenualSwim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendSwimMode(DeviceController.CommandCode.MANUAL_SWIM);
            }
        });

        findViewById(R.id.btnLight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LightDialogManager.showDialog(MainActivity.this);
            }
        });

        mBtnFishUp = (Button) findViewById(R.id.btnFishUp);
        mBtnFishLeft = (Button) findViewById(R.id.btnFishLeft);
        mBtnFishRight = (Button) findViewById(R.id.btnFishRight);

        mBtnFishUp.setOnTouchListener(this);
        mBtnFishLeft .setOnTouchListener(this);
        mBtnFishRight .setOnTouchListener(this);

        mMessageSender=new MessageSender();
        mBluetoothHelper=BluetoothHelper.getInstance();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothHelper.destroy();
    }

    @Override
    public void onSearch(BluetoothDevice device) {
    }

    @Override
    public void onConnectChanged() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                switch (v.getId()){
                    case R.id.btnFishUp:{
                        mMessageSender.send(FishDirection.FISH_UP);
                    }break;
                    case R.id.btnFishLeft:{
                        mMessageSender.send(FishDirection.FISH_LEFT);
                    }break;
                    case R.id.btnFishRight:{
                        mMessageSender.send(FishDirection.FISH_RIGHT);
                    }break;
                }
            }break;
            case MotionEvent.ACTION_UP: {
                mMessageSender.stop();
            }break;
        }
        return false;
    }

    private enum FishDirection{
        FISH_UP,
        FISH_LEFT,
        FISH_RIGHT,
    }

    private class MessageSender{
        private boolean mBeginSendMessage=false;
        private final int PRESS_INTERVAL=100;
        private Thread mThread=null;
        private FishDirection mFishDirection=FishDirection.FISH_UP;
        void send(FishDirection fishDirection){
            if (mThread==null){
                mBeginSendMessage=true;
                mThread=new Thread(runnableFishControl);
                mThread.start();
            }
            mFishDirection=fishDirection;
        }
        void stop(){
            mBeginSendMessage=false;
            mThread=null;
        }

        private Runnable runnableFishControl = new Runnable() {
            @Override
            public void run() {
                while (mBeginSendMessage) {
                    handlerFishControl.sendEmptyMessage(0);
                    try {
                        Thread.sleep(PRESS_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        private Handler handlerFishControl = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (mFishDirection){
                    case FISH_UP:{
                        DeviceController.sendSwimDirection(DeviceController.CommandCode.FISH_UP);
                    }break;
                    case FISH_LEFT:{
                        DeviceController.sendSwimDirection(DeviceController.CommandCode.FISH_LEFT);
                    }break;
                    case FISH_RIGHT:{
                        DeviceController.sendSwimDirection(DeviceController.CommandCode.FISH_RIGHT);
                    }break;
                    default:return;
                }
            }
        };
    }
}
