package com.justdestruction.gamemodlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.topjohnwu.superuser.io.SuFile;
import com.topjohnwu.superuser.io.SuRandomAccessFile;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity
{
    public static MainActivity instance;

    public String gamePgk = "com.justdestruction.modtest";

    public Button launchMenuWithGame;
    public Button launchMenuWithoutGame;
    public Button wipeLib;
    public Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        launchMenuWithGame = (Button)findViewById(R.id.buttonLaunchModsWithGame);
        launchMenuWithoutGame = (Button)findViewById(R.id.buttonLaunchModsWithoutGame);
        wipeLib = (Button)findViewById(R.id.buttonWipeLib);
        exit = (Button)findViewById(R.id.buttonExit);

        launchMenuWithGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (isAppInstalled()){
                    StartGame();
                    StartFloatingWindow();
                }
                else
                {
                    ShowMessageBox("Error", "Game not installed, make sure to not be retarded");
                }
            }
        });

        launchMenuWithoutGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartFloatingWindow();
            }
        });

        wipeLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //wipeLib(gamePgk);
                ShowMessageBox("Lol", "This doesn't work yet");
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActivity.instance.stopService();
                FloatingActivity.instance.stopSelf();
            }
        });
    }

    public void StartGame()
    {
        try
        {
            Intent intent = getPackageManager().getLaunchIntentForPackage(gamePgk);
            startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void StartFloatingWindow()
    {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
        MainActivity.this.startService(new Intent(this, FloatingActivity.class));
    }

    public boolean isAppInstalled() {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(gamePgk);
        if (mIntent != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void ShowMessageBox(String title, String message)
    {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton("Ok", (dialogInterface, i) -> System.exit(0))
                .show();
    }

/*
    public void wipeLib(String paramString) {
        try
        {
            ZipUtil.unpackEntry(new SuFile(MainActivity.this.getPackageManager().getApplicationInfo(paramString, 0).sourceDir), "lib/armeabi-v7a/libil2cpp.so", new File(MainActivity.this.getApplicationInfo().dataDir + "/data/data/" + paramString + "/lib/libil2cpp.so"));
        }
        catch (PackageManager.NameNotFoundException paramString1)
        {
            paramString1.printStackTrace();
        }
    }
    */
}