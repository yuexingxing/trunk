package com.zhiduan.crowdclient.zxing.encoding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class QRCodeEncoder {
	private String qrcode = "";

	/** 
	 * ���ַ���ɶ�ά�� 
	 * @param str 
	 * @author zhouzhe@lenovo-cw.com 
	 * @return 
	 * @throws WriterException 
	 */  
	public Bitmap Create2DCode(String str) throws WriterException {  
		//��ɶ�ά����,����ʱָ����С,��Ҫ�����ͼƬ�Ժ��ٽ�������,�����ģ����ʶ��ʧ�� 
		qrcode =  str;
		BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 300, 300);  
		int width = matrix.getWidth();  
		int height = matrix.getHeight();  
		//��ά����תΪһά��������,Ҳ����һֱ��������  
		int[] pixels = new int[width * height];  
		for (int y = 0; y < height; y++) {  
			for (int x = 0; x < width; x++) {  
				if(matrix.get(x, y)){  
					pixels[y * width + x] = 0xff000000;  
				}  
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 
		//ͨ�������������bitmap,����ο�api 
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
		return bitmap;
	}

	@SuppressWarnings("deprecation")
	public boolean  WriteBitmap(Bitmap bm){

		File folder = new File(Environment.getExternalStorageDirectory() + "/code/");

		if(!folder.exists()){// ����ļ��в����ڣ�����һ��
			folder.mkdirs(); //����Ҫ��.mkdirs()�����������ļ��в�����ʱ�������Զ�����
		}                  //�������.mkdir()�����򲻻��Զ�����

		SimpleDateFormat simpDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String strFormat = simpDate.format(new Date());
		
		File file = new File(folder.getAbsolutePath(), strFormat+".png");

		try {
			FileOutputStream out = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.v("qrcode", "FileNotFoundException");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.v("qrcode", "IOException2");
			return false;
		}
		return true;
	}
}
