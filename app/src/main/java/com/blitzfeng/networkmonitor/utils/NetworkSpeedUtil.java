package com.blitzfeng.networkmonitor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by blitzfeng on 2016/3/9.
 */
public class NetworkSpeedUtil {
    private static String networkType;
    /**
     *
     * @return
     */
    private static  int getTotalReceivedBytes(Context context) {
        String line;
        String[] segs;
        int received = 0;
        int i;
        int tmp = 0;
        boolean isNum;
        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            setNetworkType(networkInfo.getTypeName());
            FileReader fr = new FileReader("/proc/net/dev");
            BufferedReader in = new BufferedReader(fr, 500);
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (!TextUtils.isEmpty(networkType)&&line.startsWith(networkType) ) {
        //            System.out.println("line="+line);
                    segs = line.split(":")[1].split(" ");
                    for (i = 0; i < segs.length; i++) {
                        isNum = true;
                        try {
                            tmp = Integer.parseInt(segs[i]);
                        } catch (Exception e) {
                            isNum = false;
                        }
                        if (isNum == true) {
                            received = received + tmp;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            return -1;
        }
        return received;
    }
    public static void setNetworkType(String typeName){
        if(TextUtils.isEmpty(typeName))
            networkType = "";
        else if(typeName.equalsIgnoreCase("wifi"))
            networkType = "wlan";
        else if(typeName.equalsIgnoreCase("mobile"))
            networkType = "rmnet";
    }
    public static int getNetworkSpeed(Context context){
        long t = TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes();
       /* float speed = 0;
        int olderSpeed =  getTotalReceivedBytes(context);*/
        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long t1 = TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes();
    //    System.out.println("traffic="+(t1-t)/2);
    //    int s = getTotalReceivedBytes(context);
    //    speed =(float) ((s-olderSpeed)/2);
     //   System.out.println("speed="+speed);
        return (int)(t1-t)/2;
    }
}
