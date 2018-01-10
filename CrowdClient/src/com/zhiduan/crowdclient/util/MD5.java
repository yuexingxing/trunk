package com.zhiduan.crowdclient.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** 
 * 
 * @author hexiuhui
 */
public class MD5 {

	public static String appId = "axp_packet.android";// axp_packet.android

	
	public static String getMd5Sign(Map<String, String> parms, String appkey) {

		StringBuilder sb = new StringBuilder();
		if (parms == null || parms.size() < 1) {
			return "";
		}
		List<String> keys = new ArrayList<String>(parms.keySet());

		// 参数排序 升序
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			if (parms.get(key) != null) {
				sb.append(key + parms.get(key));
			}
		}
		;

		return getMd5Sign(sb.toString(), appkey);
	}

	// md5加密
	public static String getMd5Sign(String source, String appkey) {
		return encodeSign(source + appkey);
		// Logs.i("hexiuhui---", "md5之前111: " + source + appkey);
		// Logs.i("hexiuhui---", "md5之2222: " + SignUtils2.getMd5Digest((source +
		// appkey).getBytes()));
		// return SignUtils2.getMd5Digest((source + appkey).getBytes());
	}

	public static String encodeSign(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}
		return result;
	}

	public static String encode(String source) {
		String res = null;
		try {
			res = new String(source);
			MessageDigest md = MessageDigest.getInstance("MD5");
			res = byte2hexString(md.digest(res.getBytes()));
		} catch (Exception ex) {
		}
		return res;
	}

	private static final String byte2hexString(byte[] bytes) {
		StringBuffer bf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 0x10) {
				bf.append("0");
			}
			bf.append(Long.toString(bytes[i] & 0xff, 16));
		}
		return bf.toString();
	}
}
