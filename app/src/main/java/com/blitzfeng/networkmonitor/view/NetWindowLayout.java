package com.blitzfeng.networkmonitor.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blitzfeng.networkmonitor.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by blitzfeng on 2016/3/9.
 */
public class NetWindowLayout extends FrameLayout {
    private TextView speedText,iconText;
    private View view;
    public NetWindowLayout(Context context) {
        super(context);
        init(context);
    }

    public NetWindowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NetWindowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private  void init(Context context){

        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.window,null);
        addView(view);

        speedText = (TextView) view.findViewById(R.id.tv_speed);
        iconText = (TextView) view.findViewById(R.id.icon);
    //    view.setAlpha(0.3f);
       /* float f = getFontSize();
        if(f!=0)
            speedText.setTextSize(f);*/

    }

    public void setSpeedText(String speed){
        speedText.setText(speed);
    }
    public void setNetIcon(String type){
        if(type.equalsIgnoreCase("wifi")){
            iconText.setBackgroundResource(R.drawable.wifi);
            iconText.setText("");
        }else {
            iconText.setBackgroundColor(Color.TRANSPARENT);
            iconText.setText(type);
        }
    }
    public static float getFontSize() {
        Configuration mCurConfig = null;
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object getDefaultMhod = cls.getMethod("getDefault").invoke(cls);
            Object getConfigMhod = getDefaultMhod.getClass().getMethod("getConfiguration").invoke(getDefaultMhod);
            mCurConfig = new Configuration();
            mCurConfig.updateFrom(((Configuration)getConfigMhod));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println( "Unable to retrieve font size");
        }finally {

        }
        float f = mCurConfig==null?0:mCurConfig.fontScale;
        System.out.println( "getFontSize(), Font size is " + f);
        return f;

    }
}
