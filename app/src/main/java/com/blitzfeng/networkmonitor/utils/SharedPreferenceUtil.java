package com.blitzfeng.networkmonitor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceUtil {
	private SharedPreferences userInfoSp;
	private Editor userInfoEditor;


	public SharedPreferenceUtil(Context context) {
		userInfoSp = context.getSharedPreferences("userdata1",
				Context.MODE_PRIVATE);
        userInfoEditor = userInfoSp.edit();

	}
    public void setSwitchState(boolean isChecked){
        userInfoEditor.putBoolean("isChecked",isChecked).commit();
    }
    public boolean getSwitchState(){
        return  userInfoSp.getBoolean("isChecked",false);
    }
    public void setStatusbarHeight(int height){
        userInfoEditor.putInt("statusbar",height).commit();
    }
    public int getStatusbarHeight(){
        return userInfoSp.getInt("statusbar",0);
    }

}
