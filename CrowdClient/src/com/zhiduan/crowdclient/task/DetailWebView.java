package com.zhiduan.crowdclient.task;

import com.zhiduan.crowdclient.view.RefreshFooter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.ScrollView;

/**
 * 
 * <pre>
 * Description	�Զ���weiview �������ͣ������
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-23 ����4:27:06  
 * </pre>
 */
public class DetailWebView extends WebView {
    public float oldY;
    private int t;
   

    public DetailWebView(Context context) {
        super(context);
    }

    public DetailWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float Y = ev.getY();
                float Ys = Y - oldY;
                //Ys>0��ʾ�������»���->t==0��ʾһ���������˶���
                if (Ys >0 &&t==0) {
                    //Ȼ���ö����Ǹ�scrolLview���������¼�
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                //ͬ���ǻ�û����¼�->��¼λ��
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                oldY = ev.getY();
                
                break;
            case MotionEvent.ACTION_UP:
                getParent().getParent().requestDisallowInterceptTouchEvent(true);

                break;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                    this.t=t;
        super.onScrollChanged(l, t, oldl, oldt);
    }

}
