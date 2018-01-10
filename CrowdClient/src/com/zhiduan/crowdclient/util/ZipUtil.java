package com.zhiduan.crowdclient.util;

import java.io.ByteArrayInputStream;   
import java.io.ByteArrayOutputStream;   
import java.io.IOException;   
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;   
import java.util.zip.GZIPOutputStream;   

public class ZipUtil {   
	// 压缩   
	public static String compress(String str){   
		if (str == null || str.length() == 0) {   
			return str;   
		}   
		ByteArrayOutputStream out = new ByteArrayOutputStream();   
		GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes());   
			gzip.close();   
			return out.toString("ISO-8859-1");  
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;   
	}   

	// 解压缩   
	public static String uncompress(String str){   
		if (str == null || str.length() == 0) {   
			return str;   
		}   
		ByteArrayOutputStream out = new ByteArrayOutputStream();   
		ByteArrayInputStream in;
		try {
			in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			GZIPInputStream gunzip = new GZIPInputStream(in);   
			byte[] buffer = new byte[256];   
			int n;   
			while ((n = gunzip.read(buffer)) >= 0) {   
				out.write(buffer, 0, n);   
			}   
			// toString()使用平台默认编码，也可以显式的指定如toString("GBK")   
			return out.toString();  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;   
	}   

	// 测试方法   
	public static void main(String[] args) throws IOException {   
		String temp = "l;jsafljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看safljsdoeiuoksjdfpwrp3oiruewoifrjewflk我的得到喀喀喀 看看看看看看看看  ";   
		System.out.println("原字符串="+temp);   
		System.out.println("原长="+temp.length());   
		String temp1 = ZipUtil.compress(temp);   
		System.out.println("压缩后的字符串="+temp1);   
		System.out.println("压缩后的长="+temp1.length());   
		System.out.println("解压后的字符串="+ZipUtil.uncompress(temp1));   
	}   

}  