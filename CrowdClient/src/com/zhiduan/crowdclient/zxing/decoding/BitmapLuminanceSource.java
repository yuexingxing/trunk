package com.zhiduan.crowdclient.zxing.decoding;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.LuminanceSource;

/**
 * @describe
 * 
 * @version 1.0
 * @author leon
 */
public class BitmapLuminanceSource extends LuminanceSource {

	private byte bitmapPixels[];

	public BitmapLuminanceSource(Bitmap bitmap) {
		super(bitmap.getWidth(), bitmap.getHeight());

		Log.v("data.length","bitmap.getWidth() = " + bitmap.getWidth());
		Log.v("data.length","bitmap.getHeight() = " + bitmap.getHeight());
		
		// ���ȣ�Ҫȡ�ø�ͼƬ��������������
		int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
		this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());

		// ��int����ת��Ϊbyte���飬Ҳ����ȡ����ֵ����ɫֵ������Ϊ��������
		for (int i = 0; i < data.length; i++) {
			this.bitmapPixels[i] = (byte) data[i];
		}
	}

	@Override
	public byte[] getMatrix() {
		// ����������ɺõ��������
		return bitmapPixels;
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		// ����Ҫ�õ��ƶ��е��������
		System.arraycopy(bitmapPixels, y * getWidth(), row, 0, getWidth());
		return row;
	}
}
