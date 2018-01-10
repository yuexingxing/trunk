package com.zhiduan.crowdclient.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.zhiduan.crowdclient.MyApplication;

import android.util.Base64;


public class MCrypt {

	private static final String privatekey = "L+\\~f4,Ir)b$=pkf";//私钥
	private IvParameterSpec ivspec;
	private SecretKeySpec keyspec;
	private Cipher cipher;

	private String SecretKey; // 加密key

	public MCrypt() {

		SecretKey = getEncryPtion(); // 加密key

		ivspec = new IvParameterSpec(privatekey.getBytes());

		keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

		try {
			cipher = Cipher.getInstance("AES/CBC/NOPadding");
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		} catch (NoSuchPaddingException e) {

			e.printStackTrace();
		}
	}
	/**
	 * 加密
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public byte[] encrypt(String text) throws Exception {
		if (text == null || text.length() == 0)
			throw new Exception("Empty string");
		
		byte[] encrypted = null;

		try {
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

			encrypted = cipher.doFinal(padString(text).getBytes());
		} catch (Exception e) {
			throw new Exception("[encrypt] " + e.getMessage());
		}

		return encrypted;
	}
	/**
	 * 解密
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt(String code) throws Exception {
		if (code == null || code.length() == 0)
			throw new Exception("Empty string");

		byte[] decrypted = null;

		try {
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			decrypted = cipher.doFinal(hexToBytes(code));
		} catch (Exception e) {
			throw new Exception("[decrypt] " + e.getMessage());
		}
		return decrypted;
	}

	public static String bytesToHex(byte[] data) {
		if (data == null) {
			return null;
		}

		int len = data.length;
		String str = "";
		for (int i = 0; i < len; i++) {
			if ((data[i] & 0xFF) < 16)
				str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
			else
				str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
		}
		return str;
	}

	public static byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		} else if (str.length() < 2) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
			}
			return buffer;
		}
	}

	private static String padString(String source) {
		char paddingChar = ' ';
		int size = 16;
		int x = source.length() % size;
		int padLength = size - x;

		for (int i = 0; i < padLength; i++) {
			source += paddingChar;
		}

		return source;
	}

	/**
	 * 获取加密key
	 * 
	 * @return
	 */
	public static String getEncryPtion() {

//		String mRandomData = "aixuep";
//		String mTime = "20160408114222777";
//
//		Logs.v("result", "key = " + mRandomData + "/" + mTime);
		
		String mTime = MyApplication.getInstance().sendTime;
		String mRandomData = MyApplication.getInstance().mRandom;

		String key = mTime + mRandomData + mTime;

		String MD5 = CommandTools.encryption(Base64.encodeToString(key.getBytes(), Base64.NO_WRAP));
		Logs.i("result", "MD5加密key: " + MD5);
		return MD5;
	}

}