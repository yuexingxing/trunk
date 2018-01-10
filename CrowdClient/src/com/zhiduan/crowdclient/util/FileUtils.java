package com.zhiduan.crowdclient.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class FileUtils {

	// public static String fileName;
	// public static String filePath;

	public static void savePic(Bitmap bmp, String strBillcode, String filePath,
			String fileName) {

		if (bmp == null) {
			return;
		}

		// fileName = strBillcode + "_" + CommandTools.getBillNoByNowTime() +
		// ".jpg";
		// filePath = CommandTools.SDPATH + File.separator
		// + CommandTools.getTimesss() + File.separator + fileName;

		// bmp = rotateBitmapByDegree(bmp, 90);

		save(bmp, filePath, fileName);
	}

	/**
	 * 将byte数组转换成InputStream
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static InputStream byteTOInputStream(byte[] in) throws Exception {

		ByteArrayInputStream is = new ByteArrayInputStream(in);
		return is;
	}

	private static void save(Bitmap bitmap, String filePath, String fileName) {

		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs(); // 创建文件夹
		}

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos); // 向缓冲区之中压缩图片
			bos.flush();
			bos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 将图片按照某个角度进行旋转
	 * 
	 * @param bm
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float temp = ((float) height) / ((float) width);
		int newHeight = (int) ((newWidth) * temp);
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		bitmap.recycle();
		return resizedBitmap;
	}

	// 检查文件是否存在
	public static boolean isFileExist(String strFileName) {
		if (strFileName == null) {
			return false;
		}
		File file = new File(strFileName);
		return file.exists();
	}

	// 创建本项目所使用的目录
	public static void creatDirsIfNeed(String strDirName) {
		File file = new File(strDirName);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	// 删除文件夹
	public static void deleteDir(String strDir) {
		deleteFiles(new File(strDir));
	}

	public static void deleteFiles(File file) {
		// 判断文件是否存在
		if (file.exists()) {
			// 判断是否是文件
			if (file.isFile()) {
				file.delete();
			}
			// 否则如果它是一个目录
			else if (file.isDirectory()) {
				// 声明目录下所有的文件 files[];
				File files[] = file.listFiles();

				// 遍历目录下所有的文件
				for (int i = 0; i < files.length; i++) {
					deleteFiles(files[i]);
				}
			}
//			file.delete();
		} else {
			// do nothing
		}
	}

}
