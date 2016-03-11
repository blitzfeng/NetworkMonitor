package com.blitzfeng.networkmonitor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blitzfeng.networkmonitor.R;

import java.util.zip.Inflater;

/**
 * Created by blitzfeng on 2016/3/11.
 */
public class FlowListLayout extends RelativeLayout{
    public FlowListLayout(Context context) {
        super(context);
        init(context);
    }

    public FlowListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlowListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater mInflater = LayoutInflater.from(context);
        View rootView = mInflater.inflate(R.layout.flow_list,null);
    }
}
