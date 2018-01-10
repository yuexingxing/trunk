package com.zhiduan.crowdclient.menuindex.filterlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * 控件里面的文字和图片显示居中
 * @author hexiuhui
 * 
 */
public class DrawableCenterRadioButton extends RadioButton{

	public DrawableCenterRadioButton(Context context) {
		super(context);
	}
	
	public DrawableCenterRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public DrawableCenterRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	 Drawable[] drawables = getCompoundDrawables();
         if (drawables != null) {
             Drawable drawableLeft = drawables[0];
             if (drawableLeft != null) {
                 float textWidth = getPaint().measureText(getText().toString());
                 int drawablePadding = getCompoundDrawablePadding();
                 int drawableWidth = 0;
                 drawableWidth = drawableLeft.getIntrinsicWidth();
                 float bodyWidth = textWidth + drawableWidth + drawablePadding;
                 canvas.translate((getWidth() - bodyWidth) / 2, 0);
             }
         }
         super.onDraw(canvas);
    }
}
