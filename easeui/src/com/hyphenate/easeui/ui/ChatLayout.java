package com.hyphenate.easeui.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class ChatLayout extends RelativeLayout{

	private int[] mInsets = new int[4];
	
	public ChatLayout(Context context) {
		super(context);
	}
	
	public ChatLayout(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public ChatLayout(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	public final int[] getInsets() {
	    return mInsets;
	}
	
	@Override
	protected final boolean fitSystemWindows(Rect insets) {

	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        mInsets[0] = insets.left;
	        mInsets[1] = insets.top;
	        mInsets[2] = insets.right;
	        return super.fitSystemWindows(insets);
	    } else {
	        return super.fitSystemWindows(insets);
	    }
	}
}
