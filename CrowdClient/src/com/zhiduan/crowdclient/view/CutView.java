package com.zhiduan.crowdclient.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义 裁剪框
 */
public class CutView extends View {
	private Paint paint = new Paint();
	private Paint borderPaint = new Paint();
	private Paint textPaint = new Paint();

	/** 自定义顶部栏高度，如不是自定义，则默认为0即可 */
	private int customTopBarHeight = 0;
	/** 裁剪框长宽比，默认4：3 */
	private double cutRatio = 0.75;
	/** 裁剪框宽度 */
	private int cutWidth = -1;
	/** 裁剪框高度 */
	private int cutHeight = -1;
	/** 裁剪框左边空留宽度 */
	private int cutLeftMargin = 0;
	/** 裁剪框上边空留宽度 */
	private int cutTopMargin = 0;
	/** 裁剪框边框宽度 */
	private int cutBorderWidth = 1;
	private boolean isSetMargin = false;
	private OnDrawListenerComplete listenerComplete;

	public CutView(Context context) {
		super(context);
	}

	public CutView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CutView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = this.getWidth();
		int height = this.getHeight();
		// 如没有显示设置裁剪框高度和宽度，取默认值
		if (cutWidth == -1 || cutHeight == -1) {
			cutWidth = width - 50;
			cutHeight = (int) (cutWidth * cutRatio);
			// 横屏
			if (width > height) {
				cutHeight = height - 50;
				cutWidth = (int) (cutHeight / cutRatio);
			}
		}

		// 如没有显示设置裁剪框左和上预留宽度，取默认值
		if (!isSetMargin) {
			cutLeftMargin = (width - cutWidth) / 2;
			cutTopMargin = (height - cutHeight) / 2;
		}

		// 画阴影
		paint.setAlpha(100);
		
		// top
		canvas.drawRect(0, customTopBarHeight, width, cutTopMargin, paint);
		// left
		canvas.drawRect(0, cutTopMargin, cutLeftMargin, cutTopMargin + cutHeight, paint);
		// right
		canvas.drawRect(cutLeftMargin + cutWidth, cutTopMargin, width, cutTopMargin + cutHeight, paint);
		// bottom
		canvas.drawRect(0, cutTopMargin + cutHeight, width, height, paint);

		//画字体
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(34);
		canvas.drawText("请保持画面置于本框内进行上传", cutLeftMargin, cutTopMargin - 10, textPaint);

		// 画边框
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setColor(Color.WHITE);
		borderPaint.setStrokeWidth(cutBorderWidth);

//		canvas.drawRect(cutLeftMargin, cutTopMargin, cutLeftMargin + cutWidth, cutTopMargin + cutHeight, borderPaint);
		
		if (listenerComplete != null) {
			listenerComplete.onDrawCompelete();
		}
	}

	public int getCustomTopBarHeight() {
		return customTopBarHeight;
	}

	public void setCustomTopBarHeight(int customTopBarHeight) {
		this.customTopBarHeight = customTopBarHeight;
	}

	public double getCutRatio() {
		return cutRatio;
	}

	public void setCutRatio(double cutRatio) {
		this.cutRatio = cutRatio;
	}

	public int getCutWidth() {
		// 减cutBorderWidth原因：截图时去除边框白线
		return cutWidth - cutBorderWidth;
	}

	public void setCutWidth(int cutWidth) {
		this.cutWidth = cutWidth;
	}

	public int getCutHeight() {
		return cutHeight - cutBorderWidth;
	}

	public void setCutHeight(int cutHeight) {
		this.cutHeight = cutHeight;
	}

	public int getCutLeftMargin() {
		return cutLeftMargin + cutBorderWidth;
	}

	public void setCutLeftMargin(int cutLeftMargin) {
		this.cutLeftMargin = cutLeftMargin;
		isSetMargin = true;
	}

	public int getCutTopMargin() {
		return cutTopMargin + cutBorderWidth;
	}

	public void setCutTopMargin(int cutTopMargin) {
		this.cutTopMargin = cutTopMargin;
		isSetMargin = true;
	}

	public void addOnDrawCompleteListener(OnDrawListenerComplete listener) {
		this.listenerComplete = listener;
	}

	public void removeOnDrawCompleteListener() {
		this.listenerComplete = null;
	}

	/**
	 * 裁剪区域画完时调用接口
	 * 
	 * @author Cow
	 * 
	 */
	public interface OnDrawListenerComplete {
		public void onDrawCompelete();
	}

}
