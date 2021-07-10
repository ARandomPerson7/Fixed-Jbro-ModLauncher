package com.justdestruction.gamemodlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.topjohnwu.superuser.io.SuFile;
import com.topjohnwu.superuser.io.SuFileInputStream;
import com.topjohnwu.superuser.io.SuFileOutputStream;
import com.topjohnwu.superuser.io.SuRandomAccessFile;

import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.view.Gravity.LEFT;

public class FloatingActivity extends Service
{
    public static FloatingActivity instance;

    private static Method method;
    private static Object ob;
    public View mFloatingView;
    private LinearLayout patches;
    private WindowManager windowManager;
    private static Context ctx;

    private int getResID(String name,String type){
        return getResources().getIdentifier(name, type, getPackageName());
    }
    private int getLayout(String name){
        return getResID(name,"layout");
    }

    private int getID(String name){
        return getResID(name,"id");
    }
    public static String hex(int n) {
        return String.format("%2s", Integer.toHexString(n)).replace(' ', '0');
    }

    public IBinder onBind(Intent paramIntent)
    {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        this.mFloatingView = LayoutInflater.from(this).inflate(R.layout.activity_floating, null);

        try
        {
            ob = Injector.class.newInstance();
            method = Injector.class.getMethod("Init", View.class, Context.class, SuRandomAccessFile.class);
            method.setAccessible(true);
            FloatButton();
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void FloatButton() {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= 26) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | LEFT;
        params.x = 0;
        params.y = 100;
        windowManager.addView(mFloatingView, params);
        final View floter = mFloatingView.findViewById(getID("floater_container"));
        final View menu = mFloatingView.findViewById(getID("menu_container"));
        patches = (LinearLayout) mFloatingView.findViewById(getID("patches"));

        floter.setVisibility(View.VISIBLE);
        menu.setVisibility(View.GONE);

        ((ImageView) mFloatingView.findViewById(getID("mclose"))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floter.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
            }
        });

        mFloatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int differenceX = (int) Math.abs(initialTouchX - event.getRawX());
                        int differenceY = (int) Math.abs(initialTouchY - event.getRawY());
                        if (differenceX < 10 && differenceY < 10 && floter.getVisibility() == View.VISIBLE) {
                            floter.setVisibility(View.GONE);
                            menu.setVisibility(View.VISIBLE);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
        try
        {
            Object Locall = SuRandomAccessFile.open("/data/data/" + MainActivity.instance.gamePgk + "/lib/libil2cpp.so", "rw");
            method.invoke(ob, mFloatingView, this, Locall);
        }
        catch (Exception ea)
        {
            ea.printStackTrace();
            stopSelf();
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (this.mFloatingView != null) {
            windowManager.removeView(this.mFloatingView);
        }
    }

    public void stopService()
    {
        stopSelf();
    }
}