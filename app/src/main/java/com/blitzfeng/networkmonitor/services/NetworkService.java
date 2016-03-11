package com.blitzfeng.networkmonitor.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blitzfeng.networkmonitor.utils.MobileNetTypeUtil;
import com.blitzfeng.networkmonitor.utils.NetworkSpeedUtil;
import com.blitzfeng.networkmonitor.utils.SharedPreferenceUtil;
import com.blitzfeng.networkmonitor.view.NetWindowLayout;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkService extends Service {
    private final static int GET_SPEED = 0;
    public final static String NETWORK_CHANGE_ACTION = "network changed";

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private WindowManager.LayoutParams mLayoutParams;
    private MonitorReceiver monitorReceiver;
    private WindowManager windowManager;
    private NetWindowLayout netWindow;
    private boolean isRunning = false;
    private String netType;
    private Timer timer;
    private TimerTask task;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_SPEED:
            //        System.out.println("msg "+ (String)msg.obj);
                    netWindow.setSpeedText((String)msg.obj);

                    break;
            }
        }
    };

    public NetworkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isRunning) {
            setWindow();
            getNetType();
            startTask();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();

        monitorReceiver = new MonitorReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_CHANGE_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(monitorReceiver,filter);

    }
    private void getNetType(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo!=null) {
            netType = networkInfo.getTypeName();
            if(netType.equalsIgnoreCase("wifi"))
                netWindow.setNetIcon(netType);
            else if(netType.equalsIgnoreCase("mobile"))
                netWindow.setNetIcon(getMobileNet(networkInfo));
        }
    }

    private String getMobileNet(NetworkInfo networkInfo) {
        int value = MobileNetTypeUtil.getNetworkClass(networkInfo.getSubtype());
        if(value==MobileNetTypeUtil.NETWORK_CLASS_2_G)
            return "2G";
        else if(value==MobileNetTypeUtil.NETWORK_CLASS_3_G)
            return "3G";
        else if(value==MobileNetTypeUtil.NETWORK_CLASS_4_G)
            return "4G";
        else
            return "M";
    }

    private void setWindow() {
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
       mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.x = 0;
        mLayoutParams.gravity = Gravity.TOP;
     //   mLayoutParams.y = 0;
        mLayoutParams.width = 200;
        if(getStatusBarHeight(this)==0)
            mLayoutParams.height = 50;
        else
            mLayoutParams.height = getStatusBarHeight(this);
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.format = PixelFormat.TRANSPARENT;
        netWindow = new NetWindowLayout(this);
    //    System.out.println("-----------mLayoutParams.x="+mLayoutParams.x);
        netWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        /*x = event.getRawX();
                        y = event.getRawY();*/
        //                System.out.println("x="+x+"-------mTouchStartX="+mTouchStartX);
                        break;
                    case MotionEvent.ACTION_MOVE:
                       /* mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        x = event.getRawX();
                        y = event.getRawY();*/
                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        updateViewPosition();
                        mTouchStartX = 0;
                        mTouchStartY = 0;
                        break;
                }

                return true;
            }
            private void updateViewPosition(){
                mLayoutParams.x = (int)(x+mTouchStartX-540);
     //           System.out.println("mLayoutParams.x="+mLayoutParams.x);
                mLayoutParams.y = (int)(y-mTouchStartY);
                windowManager.updateViewLayout(netWindow,mLayoutParams);
            }
        });

        windowManager.addView(netWindow,mLayoutParams);
    }

    /**
     * 获取statusbar高度
     * @param context
     * @return
     */
    private int getStatusBarHeight(Context context){
   //     return new SharedPreferenceUtil(this).getStatusbarHeight();
        int statusBarHeight=0;
        try {
            Class clazz=Class.forName("com.android.internal.R$dimen");
            Object object=clazz.newInstance();
            Field field=clazz.getField("status_bar_height");
            //反射出该对象中status_bar_height字段所对应的在R文件的id值
            //该id值由系统工具自动生成,文档描述如下:
            //The desired resource identifier, as generated by the aapt tool.
            int id = Integer.parseInt(field.get(object).toString());
    //        System.out.println("id="+id);
            //依据id值获取到状态栏的高度,单位为像素
            statusBarHeight = context.getResources().getDimensionPixelSize(id);
    //        System.out.println("statusBarHeight="+statusBarHeight+"pixel");
            } catch (Exception e) {
            // TODO: handle exception
            }
        return statusBarHeight;
    }

    /**
     * 开启获取网速任务
     */
    private void startTask(){

        task = new TimerTask() {
            @Override
            public void run() {
                isRunning = true;
                int speed = NetworkSpeedUtil.getNetworkSpeed(NetworkService.this);
                String formatSpeed = format(speed);
        //        System.out.println("speed="+formatSpeed);
                Message msg = new Message();
                msg.what = GET_SPEED;
                msg.obj = formatSpeed;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task,5000,2000);
    }

    private String format(int speed) {
        if(speed>1024*1024)
            return speed/(1024*1024)+" M/s";
        else if(speed>1024)
            return speed/1024+" K/s";
        else
            return speed+" B/s";
    }

    /**
     * 获取屏幕尺寸
     * @return
     */
    private Point getScreen(){
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }
    @Override
    public void onDestroy() {
        if(monitorReceiver !=null)
            unregisterReceiver(monitorReceiver);
        windowManager.removeView(netWindow);
        timer.cancel();
    //    stopSelf();
        System.out.println("onDestroy");
        super.onDestroy();
    }

    private class MonitorReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){//网络变化
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                if(networkInfo!=null&&networkInfo.isAvailable())
          //         if(!netType.equalsIgnoreCase(networkInfo.getTypeName())){
                       if(netType.equalsIgnoreCase("wifi"))
                           netWindow.setNetIcon(netType);
                       else if(netType.equalsIgnoreCase("mobile"))
                           netWindow.setNetIcon(getMobileNet(networkInfo));
                       netType = networkInfo.getTypeName();
                       NetworkSpeedUtil.setNetworkType(netType);
          //         }


            }else if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){//点亮屏幕
                windowManager.addView(netWindow,mLayoutParams);
                timer.schedule(task,2000,2000);
            }else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){//锁屏
                timer.cancel();
                windowManager.removeView(netWindow);
            }
        }
    }
}
