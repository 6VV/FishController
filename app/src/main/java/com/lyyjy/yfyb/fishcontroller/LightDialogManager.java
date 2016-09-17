package com.lyyjy.yfyb.fishcontroller;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/17.
 */
public class LightDialogManager {
    private static final String COLOR_WHITE_NAME="WHITE";
    private static final String COLOR_YELLOW_NAME="YELLOW";
    private static final String COLOR_VIOLET_NAME="VIOLET";
    private static final String COLOR_RED_NAME="RED";
    private static final String COLOR_CYAN_NAME="CYAN";
    private static final String COLOR_GREEN_NAME="GREEN";
    private static final String COLOR_BLUE_NAME="BLUE";
    private static final String COLOR_BLACK_NAME="BLACK";

    private static final HashMap<String,Integer> COLOR_MAP=new HashMap<String,Integer>(){
        {
            put(COLOR_WHITE_NAME,0xFFFFFFFF);
            put(COLOR_YELLOW_NAME,0xFFFFFF00);
            put(COLOR_VIOLET_NAME,0xFFFF00FF);
            put(COLOR_RED_NAME,0xFFFF0000);
            put(COLOR_CYAN_NAME,0xFF00FFFF);
            put(COLOR_GREEN_NAME,0xFF00FF00);
            put(COLOR_BLUE_NAME,0xFF0000FF);
            put(COLOR_BLACK_NAME,0xFF000000);
        }
    };

    public static void showDialog(Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);

        LayoutInflater inflater=LayoutInflater.from(context);
        LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.dialog_light, null);

        builder.setView(layout);

        layout.findViewById(R.id.btnWhite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendColor(toColorByte(COLOR_MAP.get(COLOR_WHITE_NAME)));
            }
        });

        layout.findViewById(R.id.btnYellow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendColor(toColorByte(COLOR_MAP.get(COLOR_YELLOW_NAME)));
            }
        });

        layout.findViewById(R.id.btnViolet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendColor(toColorByte(COLOR_MAP.get(COLOR_VIOLET_NAME)));
            }
        });
        layout.findViewById(R.id.btnRed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendColor(toColorByte(COLOR_MAP.get(COLOR_RED_NAME)));
            }
        });
        layout.findViewById(R.id.btnCyan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendColor(toColorByte(COLOR_MAP.get(COLOR_CYAN_NAME)));
            }
        });
        layout.findViewById(R.id.btnGreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendColor(toColorByte(COLOR_MAP.get(COLOR_GREEN_NAME)));
            }
        });
        layout.findViewById(R.id.btnBlue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendColor(toColorByte(COLOR_MAP.get(COLOR_BLUE_NAME)));
            }
        });
        layout.findViewById(R.id.btnBlack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceController.sendColor(toColorByte(COLOR_MAP.get(COLOR_BLACK_NAME)));
            }
        });
        builder.setNegativeButton("取消",null);

        builder.show();
    }

    private static byte toColorByte(int color){
        byte result=0;
        if ((color&0x000000FF)!=0){
            result+=1;
        }
        if ((color&0x0000FF00)!=0){
            result+=2;
        }
        if ((color&0x00FF0000)!=0){
            result+=4;
        }
        return result;
    }
}
