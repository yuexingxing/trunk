package com.zhiduan.crowdclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CrashLogUtil {

	/**
	 * 往175服务器发送异常日志
	 * 
	 * @param strTitle
	 * @param strBody
	 */
	public static void send_log(String strTitle, String strBody) {
		String urlString = "http://pda.shzhiduan.com:15007/Crashlog.ashx";
		String SvrErrMsg = "";
			String res = "";
			try {
				// 转成指定格式
				byte[] requestData = (strTitle + strBody).getBytes("UTF-8");

				HttpURLConnection conn = null;
				DataOutputStream outStream = null;

				String MULTIPART_FORM_DATA = "multipart/form-data";
				// 构造一个post请求的http头
				StringBuilder sb = new StringBuilder();
				URL url = new URL(urlString); // 服务器地址

				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // 允许输入
				conn.setDoOutput(true); // 允许输出
				conn.setUseCaches(false); // 不使用caches
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA);
				conn.setRequestProperty("Content-Length", Long.toString(requestData.length));
				//conn.setRequestProperty("DEVICEID", DeviceID);
				//conn.setRequestProperty("PDASFTVER", Constans.PDASftVER); 
				// 请求参数内容, 获取输出到网络的连接流对象
				outStream = new DataOutputStream(conn.getOutputStream());
				outStream.write(requestData, 0, requestData.length);
				outStream.flush();
				outStream.close();

				int cah = conn.getResponseCode();
				if (cah != 200) {
					res = "-1"; // 错误的标志是 不 null
					SvrErrMsg = "调用后台服务失败,错误代码:" + cah + " ; 错误消息：" + conn.getResponseMessage();
					return;
				}

				// 方法2
				InputStream inputStream = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String responseStr = "";

				SvrErrMsg = "";
				responseStr = reader.readLine();
				reader.close();
			} catch (Exception e) {
				SvrErrMsg = e.getMessage();
			}
		}

	
}
