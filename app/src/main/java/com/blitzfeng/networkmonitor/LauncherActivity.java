package com.blitzfeng.networkmonitor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.blitzfeng.networkmonitor.services.NetworkService;
import com.blitzfeng.networkmonitor.utils.SharedPreferenceUtil;

public class LauncherActivity extends Activity{

    private Switch networkSwitch;
    private SharedPreferenceUtil sp;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        sp = new SharedPreferenceUtil(this);
        intent = new Intent(this,NetworkService.class);
        initView();
        setStatusbar();
    }

    private void initView() {
        networkSwitch = (Switch) findViewById(R.id.switch_window);
        boolean state = sp.getSwitchState();
        networkSwitch.setChecked(state);
        setSwitch(state);
        /*if(state){

        }*/
    //    networkSwitch.setOnClickListener(this);
        networkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.setSwitchState(isChecked);
                setSwitch(isChecked);
            }
        });
    }
    private void setSwitch(boolean isChecked){
        if(isChecked)
            LauncherActivity.this.startService(intent);
        else
            LauncherActivity.this.stopService(intent);
    }
    private void setStatusbar(){
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        sp.setStatusbarHeight(frame.top);
        System.out.println("top="+frame.top);
    }
}
