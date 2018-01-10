package com.zhiduan.crowdclient.net;

import java.util.concurrent.ThreadPoolExecutor;

import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.LoginUtil;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.SingleLoginDialog;
import com.zhiduan.crowdclient.view.SingleLoginDialog.ResultCallback;

/**
 * 网络请求
 * 
 */
public class LoadTextNetTask extends AsyncNetTask {
	public LoadTextNetTask(ThreadPoolExecutor executor, LoadTextParams params) {
		super(executor, params);
		setType(AsyncNetTask.TaskType.GET_TEXT);
	}

	protected NetTaskResult doLoadTextWork(NetTaskParams... params) {
		LoadResult preResult = NetTaskUtil.doLoadWork(this, params);
		LoadTextResult pstResult = new LoadTextResult();
		pstResult.m_nStatusCode = preResult.m_nStatusCode;
		if (preResult.m_nResultCode == 0) {
			pstResult.m_strContent = new String(preResult.buf);
			pstResult.m_nResultCode = 0;
			pstResult.m_strResultDesc = "";
		} else {
			pstResult.m_nResultCode = preResult.m_nResultCode;
			pstResult.m_strResultDesc = preResult.m_strResultDesc;
		}

		return pstResult;
	}

	@Override
	protected NetTaskResult doInBackground(NetTaskParams... params) {
		publishProgress(0L);
		AsyncTaskManager.yieldIfNeeded(this);
		publishProgress(1L, 0L, 0L);
		// return doFakeTaskWork(params);
		return doLoadTextWork(params);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(NetTaskResult result) {
		
		if (result.m_nResultCode == 0) {
			LoadTextResult mresult = (LoadTextResult) result;

			Logs.i("NetTaskUtil", mresult.m_strContent + "");
			String strData = "\"code\"" + ":" + "\"100\"";
			if (mresult.m_strContent.contains(strData)) {

				Logs.i("zdkj", "返回数据: " + "用户未登录，请重新登录");
				if(MyApplication.baseActivity != null){
					Intent intent = new Intent(MyApplication.baseActivity, LoginActivity.class);
					MyApplication.baseActivity.startActivity(intent);
					MyApplication.baseActivity.finish();
				}
//				CustomProgress.dissDialog();
//
//				SingleLoginDialog.showMyDialog(CommandTools.getGlobalActivity(), new ResultCallback() {
//					@Override
//					public void callback(boolean result) {
//						// TODO Auto-generated method stub
//						Log.i("hexiuhui============", "用户未登录，请重新登录用户未登录，请重新登录用户未登录，请重新登录用户未登录，请重新登录用户未登录，请重新登录用户未登录，请重新登录用户未登录，请重新登录用户未登录，请重新登录用户未登录，请重新登录用户未登录，请重新登录");
//						String loginName = (String) SharedPreferencesUtils.getParam(Constant.SP_LOGIN_NAME, "");
//						String loginPwd = (String) SharedPreferencesUtils.getParam(Constant.SP_LOGIN_PSD, "");
//
//						LoginUtil.login(CommandTools.getGlobalActivity(), loginName, loginPwd, null, false);
//					}
//				});
			}
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
