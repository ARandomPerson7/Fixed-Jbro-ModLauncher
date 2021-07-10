package com.justdestruction.gamemodlauncher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.topjohnwu.superuser.io.SuRandomAccessFile;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Injector
{
    private static Injector inst = new Injector();
    private static Context ctx;
    private static LinearLayout patches;
    private static SuRandomAccessFile raf;
    private static SuRandomAccessFile raf2;
    private static int swOff = -1;
    private static int swOn = -1;
    private static int txtColor = -1;
    private static float txtSize = 18.0F;

    private static byte[] Hex2b(String hex) {
        if(hex.contains(" ")) {
            hex = hex.replace(" ", "");
        }

        if(hex == null) {
            throw new IllegalArgumentException("hex == null");
        } else if(hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Unexpected hex string: " + hex);
        } else {
            byte[] result = new byte[hex.length() / 2];

            for(int i = 0; i < result.length; ++i) {
                int d1 = decodeHexDigit(hex.charAt(i * 2)) << 4;
                int d2 = decodeHexDigit(hex.charAt(i * 2 + 1));
                result[i] = (byte)(d1 + d2);
            }

            return result;
        }
    }

    @QVMProtect
    public static void Init(View paramView, Context paramContext, SuRandomAccessFile suRandomAccessFile)
    {
        ctx = paramContext;
        raf = suRandomAccessFile;
        patches = (LinearLayout)paramView.findViewById(getID("patches"));
        paramView = (LinearLayout)paramView.findViewById(getID("menu_container"));

        setTxtColor(Color.WHITE);
        setTxtSize(19.0F);
        setSwitchOnColor(Color.GREEN);
        setSwitchOffColor(Color.RED);

        addText("Mod Menu by Just Destruction");
        addSwitch("Test", new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
            {
                if (paramAnonymousBoolean)
                {
                    Injector.write(formatOffset("0x7C22E4"), "00 00 A0 E3 1E FF 2F E1");
                    return;
                }
                Injector.write(formatOffset("0x7C22E4"), "01 00 A0 E3 1E FF 2F E1");
            }
        });
    }

    public static EditText addEditText(String hint) {
        EditText localEditText = new EditText(ctx);
        localEditText.setHint(hint);
        localEditText.setLayoutParams(setParams());
        localEditText.setFocusableInTouchMode(true);
        //localEditText.setFocusable(true);
        patches.addView(localEditText);
        return localEditText;
    }

    public static SeekBar addSeekbar(int bars, SeekBar.OnSeekBarChangeListener paramOnSeekBarChangeListener)
    {
        SeekBar localSeekBar = new SeekBar(ctx);
        localSeekBar.setMax(bars);
        //localSeekBar.setThumb(ctx.getResources().getDrawable(R.drawable.j));
        localSeekBar.setLayoutParams(setParams());
        localSeekBar.setOnSeekBarChangeListener(paramOnSeekBarChangeListener);
        patches.addView(localSeekBar);
        return localSeekBar;
    }


    @SuppressLint({"ResourceType"})
    public static Spinner addSpinner(String name, String[] items, AdapterView.OnItemSelectedListener listener) {
        TextView tv = new TextView(ctx);
        tv.setText(name);
        tv.setTextSize(txtSize);
        tv.setTextColor(txtColor);
        tv.setLayoutParams(setParams());
        Spinner sp = new Spinner(ctx);
        sp.setLayoutParams(setParams());
        ArrayAdapter<?extends String> dataAdapter = new ArrayAdapter<>(ctx, 17367048, items);
        dataAdapter.setDropDownViewResource(17367049);
        sp.setAdapter(dataAdapter);
        sp.setOnItemSelectedListener(listener);
        patches.addView(tv);
        patches.addView(sp);
        return sp;
    }

    public static Switch addSwitch(String name, CompoundButton.OnCheckedChangeListener listener)
    {
        final Switch sw = new Switch(ctx);
        sw.setText(name);
        sw.setTextSize(txtSize);
        sw.setTextColor(txtColor);
        if(Build.VERSION.SDK_INT >= 16)
        {
            sw.getThumbDrawable().setColorFilter(swOff, PorterDuff.Mode.MULTIPLY);
        }
        sw.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if(Build.VERSION.SDK_INT >= 16)
                {
                    if(sw.isChecked())
                    {
                        sw.getThumbDrawable().setColorFilter(swOn, PorterDuff.Mode.MULTIPLY);
                    }
                    else
                    {
                        sw.getThumbDrawable().setColorFilter(swOff, PorterDuff.Mode.MULTIPLY);
                    }
                }
            }
        });
        sw.setOnCheckedChangeListener(listener);
        sw.setLayoutParams(setParams());
        patches.addView(sw);
        return sw;
    }

    public static CheckBox addCheckBox(String name, View.OnClickListener listener)
    {
        final CheckBox check = new CheckBox(ctx);
        check.setText(name);
        check.setTextSize(txtSize);
        check.setTextColor(txtColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            check.setTextAppearance(Gravity.CENTER);
        }
        check.setOnClickListener(listener);
        check.setLayoutParams(setParams());
        patches.addView(check);
        return check;
    }

    public static Button addButton(String name, View.OnClickListener listener)
    {
        final Button but = new Button(ctx);
        but.setText(name);
        but.setTextSize(txtSize);
        but.setTextColor(txtColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            but.setTextAppearance(Gravity.CENTER);
        }
        but.setOnClickListener(listener);
        but.setLayoutParams(setParams());
        patches.addView(but);
        return but;
    }

    public static void addReturn()
    {
        addText(" ");
    }

    public static TextView addText(String txt)
    {
        TextView localTextView = new TextView(ctx);
        localTextView.setText(txt);
        localTextView.setTextColor(txtColor);
        localTextView.setTextSize(txtSize);
        localTextView.setLayoutParams(setParams());
        patches.addView(localTextView);
        return localTextView;
    }

    public static TextView addText(String txt, int color)
    {
        TextView localTextView = new TextView(ctx);
        localTextView.setText(txt);
        localTextView.setTextColor(color);
        localTextView.setTextSize(txtSize);
        localTextView.setLayoutParams(setParams());
        patches.addView(localTextView);
        return localTextView;
    }

    public static TextView addText(String txt, float size)
    {
        TextView localTextView = new TextView(ctx);
        localTextView.setText(txt);
        localTextView.setTextColor(txtColor);
        localTextView.setTextSize(size);
        localTextView.setLayoutParams(setParams());
        patches.addView(localTextView);
        return localTextView;
    }

    public static TextView addText(String txt, float size, int color)
    {
        TextView localTextView = new TextView(ctx);
        localTextView.setText(txt);
        localTextView.setTextColor(color);
        localTextView.setTextSize(size);
        localTextView.setLayoutParams(setParams());
        patches.addView(localTextView);
        return localTextView;
    }

    private static int decodeHexDigit(char paramChar)
    {
        if ((paramChar >= '0') && (paramChar <= '9')) {
            return paramChar - '0';
        }
        if ((paramChar >= 'a') && (paramChar <= 'f')) {
            return paramChar - 'a' + 10;
        }
        if ((paramChar >= 'A') && (paramChar <= 'F')) {
            return paramChar - 'A' + 10;
        }
        throw new IllegalArgumentException("Unexpected hex digit: " + paramChar);
    }

    private static float dipToPixels()
    {
        return TypedValue.applyDimension(1, 8.0F, ctx.getResources().getDisplayMetrics());
    }

    private static int getID(String paramString)
    {
        return getResID(paramString, "id");
    }

    private static int getIndex(int[] paramArrayOfInt, int paramInt)
    {
        int i = 0;
        while (i < paramArrayOfInt.length)
        {
            if (paramArrayOfInt[i] == paramInt) {
                return i;
            }
            i += 1;
        }
        return -1;
    }

    private static int getResID(String paramString1, String paramString2)
    {
        return ctx.getResources().getIdentifier(paramString1, paramString2, ctx.getPackageName());
    }

    public static int getSwitchOffColor()
    {
        return swOff;
    }

    public static int getSwitchOnColor()
    {
        return swOn;
    }

    public static int getTxtColor()
    {
        return txtColor;
    }

    private static String hex(int paramInt)
    {
        return String.format("%2s", new Object[] { Integer.toHexString(paramInt) }).replace(' ', '0');
    }

    private static byte[] iTob(int[] paramArrayOfInt)
    {
        byte[] arrayOfByte = new byte[paramArrayOfInt.length];
        int i = 0;
        while (i < paramArrayOfInt.length)
        {
            arrayOfByte[i] = ((byte)(paramArrayOfInt[i] & 0xFF));
            i += 1;
        }
        return arrayOfByte;
    }

    private static byte[] sTob(String paramString)
    {
        byte[] arrayOfByte = new byte[paramString.length()];
        int i = 0;
        while (i < paramString.length())
        {
            arrayOfByte[i] = ((byte)paramString.charAt(i));
            i += 1;
        }
        return arrayOfByte;
    }

    private static LinearLayout.LayoutParams setParams()
    {
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -1);
        localLayoutParams.gravity = 16;
        return localLayoutParams;
    }

    public static void setSwitchOffColor(int paramInt)
    {
        swOff = paramInt;
    }

    public static void setSwitchOnColor(int paramInt)
    {
        swOn = paramInt;
    }

    public static void setTxtColor(int paramInt)
    {
        txtColor = paramInt;
    }

    public static void setTxtSize(float paramFloat)
    {
        txtSize = paramFloat;
    }

    private static void write(int paramInt, String paramString)
    {
        try
        {
            raf.seek(paramInt);
            raf.write(Hex2b(paramString));
            return;
        }
        catch (IOException paramString1)
        {
            paramString1.printStackTrace();
        }
    }

    private static int formatOffset(String offset)
    {
        if(offset.contains("0x"))
        {
            return Integer.parseInt(offset.replace("0x", ""), 16);
        }
        if(offset.contains("0X"))
        {
            return Integer.parseInt(offset.replace("0X", ""), 16);
        }
        return Integer.parseInt(offset, 16);
    }

    private static void write(int paramInt, int[] paramArrayOfInt)
    {
        try
        {
            raf.seek(paramInt);
            raf.write(iTob(paramArrayOfInt));
            return;
        }
        catch (IOException paramArrayOfInt1)
        {
            paramArrayOfInt1.printStackTrace();
        }
    }

    private static void writeS(int paramInt, String paramString)
    {
        try
        {
            raf.seek(paramInt);
            raf.write(sTob(paramString));
            return;
        }
        catch (IOException paramString1)
        {
            paramString1.printStackTrace();
        }
    }

    private static void write2nd(int paramInt, String paramString)
    {
        try
        {
            raf2.seek(paramInt);
            raf2.write(Hex2b(paramString));
            return;
        }
        catch (IOException paramString1)
        {
            paramString1.printStackTrace();
        }
    }
}
