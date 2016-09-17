package com.lyyjy.yfyb.fishcontroller;

/**
 * Created by Administrator on 2016/9/17.
 */
public class DeviceController {
    public enum CommandCode{
        FISH_UP,
        FISH_LEFT,
        FISH_RIGHT,
        AUTO_SWIM,
        MANUAL_SWIM,
    }

    public static final byte BACK_SUCCESS = (byte) 0x68;    //设置成功
    public static final byte BACK_RESET_NAME = (byte) 0x02;   //设置名字成功
    public static final byte BACK_HEART_HIT=(byte)0x04;     //心跳
    public static final byte BACK_PROGRAM=(byte)0x06;   //程序文本

    private static final byte STATUS_HEAD_1 = (byte) 0x55; //命令头
    private static final byte STATUS_HEAD_2 = (byte) 0xAA;
    private static final byte STATUS_HEAD_3 = (byte) 0x99;
    private static final byte STATUS_HEAD_4 = (byte) 0x11;
    private static final byte STATUS_FINAL = (byte) 0xFF;    //数据尾

    public static final byte STATUS_FISH_UP =(byte)0x00;      //小鱼前进指令
    public static final byte STATUS_FISH_RIGHT =(byte)0x03;   //小鱼右转指令
    public static final byte STATUS_FISH_LEFT =(byte)0x02;    //小鱼左转指令
    public static final byte STATUS_FISH_STOP =(byte)0x20;             //小鱼停止指令

    private static final byte STATUS_MODE_MANUAL =(byte)0x00;
    private static final byte STATUS_MODE_AUTO =(byte)0x01;

    private static final byte REQUEST_CONTROL = (byte) 0x00;  //控制指令
    private static final byte REQUEST_SET_NAME = (byte) 0x02; //设置名字
    private static final byte REQUEST_RESET = (byte) 0x04;      //重置设备
    public static final byte REQUEST_COLOR=(byte)0x05;      //灯光颜色
    private static final byte REQUEST_MODE=(byte)0x06;      //游动模式
    private static final byte REQUEST_DIRECTION_PROGRAM=(byte)0x07; //方向控制程序
    private static final byte REQUEST_LIGHT_PROGRAM=(byte)0x08; //灯光控制程序

    private static byte[] COMMAND_SET_COLOR={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3, STATUS_HEAD_4, REQUEST_COLOR,0x00, STATUS_FINAL}; //设置名字
    private static byte[] COMMAND_SET_MODE={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3, STATUS_HEAD_4,REQUEST_MODE,0x00, STATUS_FINAL};       //设置游动方式
    private static final byte[] COMMAND_RESET_DEVICE={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3, STATUS_HEAD_4, REQUEST_RESET, STATUS_FINAL};  //重置设备
    public static final byte[] COMMAND_DERECTION_PROGRAM={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3,STATUS_HEAD_4,REQUEST_DIRECTION_PROGRAM};
    public static final byte[] COMMAND_LIGHT_PROGRAM={STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3,STATUS_HEAD_4,REQUEST_LIGHT_PROGRAM};

    private static byte[] FISH_COMMAND={
            STATUS_HEAD_1, STATUS_HEAD_2, STATUS_HEAD_3, STATUS_HEAD_4,
            REQUEST_CONTROL,      //控制小鱼
            0x00,                   //游动方向
            (byte) 0x03,           //速度
            STATUS_FINAL};

    public static void sendSwimDirection(CommandCode commandCode){
        BluetoothHelper bluetoothHelper=BluetoothHelper.getInstance();
        switch (commandCode){
            case FISH_LEFT:{
                setDirection(STATUS_FISH_LEFT);
                bluetoothHelper.send(FISH_COMMAND);
            }break;
            case FISH_RIGHT:{
                setDirection(STATUS_FISH_RIGHT);
                bluetoothHelper.send(FISH_COMMAND);
            }break;
            case FISH_UP:{
                setDirection(STATUS_FISH_UP);
                bluetoothHelper.send(FISH_COMMAND);
            }break;
        }
    }

    private static void setDirection(byte direction){
        FISH_COMMAND[5]=direction;
    }

    public static void sendSwimMode(CommandCode commandCode){
        switch (commandCode){
            case MANUAL_SWIM:{
                COMMAND_SET_MODE[5]= STATUS_MODE_MANUAL;
                BluetoothHelper.getInstance().send(COMMAND_SET_MODE);
            }break;
            case AUTO_SWIM:{
                COMMAND_SET_MODE[5]= STATUS_MODE_AUTO;
                BluetoothHelper.getInstance().send(COMMAND_SET_MODE);
            }break;
            default:return;
        }
    }

    public static void sendColor(byte color){
        COMMAND_SET_COLOR[5]=color;
        BluetoothHelper.getInstance().send(COMMAND_SET_COLOR);
    }
}
