package com.zhiduan.crowdclient.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;

/** 
 * 订单申诉界面
 * 
 * @author yxx
 *
 * @date 2017-1-3 下午1:46:04
 * 
 */
public class ComplainDialog{

	static Dialog dialog;
	private static String reportType;

	// 弹窗结果回调函数
	public static abstract class ComplainCallback {
		public abstract void callback(JSONObject jsonObject);
	}

	public ComplainDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showDialog(final Context context, final String takerId, final ComplainCallback callback){

		reportType = null;
		if(dialog != null){
			dialog.dismiss();
		}

		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_complain, null);

		ImageView imgClose = (ImageView) layout.findViewById(R.id.img_complain_close);
		RadioGroup radioGroup = (RadioGroup) layout.findViewById(R.id.radioGroup_complain);
		final EditText edtContent = (EditText) layout.findViewById(R.id.edt_complain_content);

		Button btnOk = (Button) layout.findViewById(R.id.btn_complain_ok);

		imgClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				closeDialog();
			}
		});

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {

				//获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				if(radioButtonId == R.id.radioButton_complain_1){
					reportType = "P_APPEAL_NOT_DELIVER";
				}else if(radioButtonId == R.id.radioButton_complain_2){
					reportType = "P_APPEAL_NOT_CONNECT";
				}else if(radioButtonId == R.id.radioButton_complain_3){
					reportType = "P_APPEAL_MONEY_QUES";
				}else if(radioButtonId == R.id.radioButton_complain_4){
					reportType = "P_APPEAL_OTHER_QUES";
				}

			}
		});

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(callback != null){
					
					if(TextUtils.isEmpty(reportType)){
						CommandTools.showToast("请选择申述原因");
						return;
					}
					
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("reportContent", edtContent.getText().toString());
						jsonObject.put("reportType", reportType);
						jsonObject.put("takerId", takerId);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					callback.callback(jsonObject);
					closeDialog();
				}
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
		setDialogWindowAttr(dialog, context);
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
	}

	//在dialog.show()之后调用
	@SuppressWarnings("deprecation")
	public static void setDialogWindowAttr(Dialog dlg,Context ctx){

		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.CENTER|Gravity.BOTTOM;
		lp.width = (int) (display.getWidth()/1.3);
		lp.height = (int) (display.getHeight()/1.3);
		dlg.getWindow().setAttributes(lp);
	}
}
