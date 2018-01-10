/**  
 * @Title: AESII.java
 * @Package com.zhiduan.axp.tools.sign
 * @Project: axp-tools
 * @Description: (用一句话描述该文件做什么)
 * @author wdx
 * @date 2016年3月31日 下午8:06:29 
 * @version V1.0  
 * ──────────────────────────────────
 * Copyright (c) 2016, 指端科技.
 */

package com.zhiduan.crowdclient.util;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.zhiduan.crowdclient.MyApplication;

import android.util.Base64;


/**
 * @ClassName: AES
 * @Description: AES解密工具
 * @author wdx
 * @date 2016年3月31日 下午8:06:29 
 *
 */

public class AES {

	//私钥AES
	private static final String privatekey = "L+\\~f4,Ir)b$=pkf";

	/**
	 * AES加密 传入明文，返回加密结果
	 * @param plaintext 加密字符串（明文）
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String plaintext){
		
		String publickey = MyApplication.getInstance().sendTime;
		String confusioncode = MyApplication.getInstance().mRandom;
		
		plaintext = CommandTools.str16(plaintext);

		try {

			//生成公钥，并MD5加密
			String key = publickey+confusioncode+publickey;

			Logs.v("Test", "明文: " + plaintext + "//" + plaintext.length());
			Logs.v("Test", "公钥: " + key);
			key = CommandTools.encryption(Base64.encodeToString(key.getBytes(), Base64.NO_WRAP));
			Logs.v("Test", "MD5加密后结果: " + key);
			//加密链
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = plaintext.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}
			byte[] plaintextBytes = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintextBytes, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(privatekey.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

			//加密
			byte[] encrypted = cipher.doFinal(plaintextBytes);

			//BASE64 编码
			String strSign = Base64.encodeToString(encrypted, Base64.NO_WRAP) + confusioncode;
			Logs.v("Test", "最终加密结果: " + strSign);

			return strSign;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}



}
