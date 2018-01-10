/*package com.zhiduan.crowdclient.swipeview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
 *//**
  * 
  * <pre>
  * Description	 自定义禁止触摸事件传递的linearlayout
  * Copyright:	Copyright (c)2016 
  * Company:		Z D technology
  * Author:		Huang hua
  * Version:		1.0  
  * Created at:	2016-8-10 下午6:36:23  
  * </pre>
  *//*
public class TouchLayout extends LinearLayout {
 
    private String TAG = TouchLayout.class.getSimpleName();
 
    public TouchLayout(Context context) {
        super(context);
 
    }
 
    public TouchLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public TouchLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setClickable(true);
    }
 
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev) ;
  
        return result;
    }
 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 
        final int action = MotionEventCompat.getActionMasked(ev);
        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Do not intercept touch event, let the child handle it
            return true;
        }
     
        return true;
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
      
         return true;
//        return true;
    }
     
}*/