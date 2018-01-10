package com.zhiduan.crowdclient.view;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
/**
 * 显示输入密码的工具类
 * 
 * @author wfq
 * 
 */
public class ImportPassword implements OnKeyListener {
	private static Context mContext;
	private final static int IMPORTPASSWORD = 0x001;
	private final static int PASSWORDERROR = 0x002;
	private static int TYPE;
	/**
	 * 显示输入密码对话框
	 * 
	 * @param context
	 */
	public static void showImportPassWord(Context context, String money, PasswordListener i) {
		mContext = context;

		if (dialog != null) {
			dialog.dismiss();
		}
		TYPE = IMPORTPASSWORD;
		
//		ad = new AlertDialog.Builder(mContext);
		ImportPassword importPassword = new ImportPassword();
		View view = importPassword.returnViewImport(R.layout.dialog_input_password, money, "请输入支付密码", false, i);
		TextView mMoney = (TextView) view.findViewById(R.id.dialog_input_password_money);
		mMoney.setTextSize(30);
		mMoney.setText("￥"+money);
//		dialog = ad.create();
//		dialog.setView(view,0,0,0,0);
//		dialog.show();
		dialog = new Dialog(mContext, R.style.dialog_no_border); 
		dialog.setContentView(view); 
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.getAttributes().width=CommandTools.dip2px(mContext, 300);
		dialog.show();
	}
	/**
	 * 显示验证码对话框
	 * 
	 * @param context
	 */
	public static void showAuthCode(Context context, PasswordListener i) {
		mContext = context;

		if (dialog != null) {
			dialog.dismiss();
		}
		TYPE = IMPORTPASSWORD;
//		ad = new AlertDialog.Builder(mContext);
		ImportPassword importPassword = new ImportPassword();
		String phone = (String) SharedPreferencesUtils.getParam( Constant.SP_LOGIN_NAME, "");
		View view = importPassword.returnViewImport(R.layout.dialog_input_password, "验证码将发送到" + phone + "手机中",
				"请输入验证码", true, i);
		long time=System.currentTimeMillis()-MyApplication.withdrawDepositTime;
		if (MyApplication.withdrawDepositTime != 0&&time>90000) {
			
			
			sendCode(phone);
		}
		
		if(MyApplication.withdrawDepositTime==0){
			sendCode(phone);
		}
//		dialog = ad.create();
//		dialog.setView(view,0,0,0,0);
//		dialog.show();
		dialog = new Dialog(mContext, R.style.dialog_no_border); 
		dialog.setContentView(view); 
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.getAttributes().width=CommandTools.dip2px(mContext, 300);
		dialog.show();
	}
	private static void sendCode(String phone) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("phone", phone);
			jsonObject.put("codeType", "paycode");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "user/code/send.htm";
		RequestUtilNet.postData(mContext, strUrl, "验证码获取中", true, jsonObject, new MyCallback() {

			@Override
			public void callback(JSONObject jsonObject) {
				MyApplication.withdrawDepositTime=System.currentTimeMillis();
				CustomProgress.dissDialog();
			}
		});
		
	}
	/**
	 * 显示错误的信息
	 * 
	 * @param context
	 * @param i
	 */
	public static void showPassWordError1(Context context, String content,String type , OnClickListener i) {

		mContext = context;
		if (dialog != null) {
			dialog.dismiss();
		}
		TYPE = PASSWORDERROR;
//		ad = new AlertDialog.Builder(mContext);
		ImportPassword importPassword = new ImportPassword();
		View view = importPassword.returnViewPassWordError(R.layout.dialog_password_error, type ,content, i);
//		dialog = ad.create();
//		dialog.setView(view,0,0,0,0);
//		dialog.show();
		dialog = new Dialog(mContext, R.style.dialog_no_border); 
		dialog.setContentView(view); 
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.getAttributes().width=CommandTools.dip2px(mContext, 300);
		dialog.show();
	}

	private ImportPassword() {
		super();
		// TODO Auto-generated constructor stub
	}

	boolean isopen = false;
	private ImageView mIvClose;
	private TextView mEt1;
	private TextView mEt2;
	private TextView mEt3;
	private TextView mEt4;
	private TextView mEt5;
	private TextView mEt6;
	private EditText mEt7;
	private int length = 0;
	private TextView mMoney;
	private TextView mTitle;

	/**
	 * 返回对话框输入密码的view
	 * 
	 * @param r
	 * @param money
	 * @param i
	 * @return
	 */
	private View returnViewImport(int r, String money, String title, boolean type, final PasswordListener i) {
		View view = View.inflate(mContext, r, null);
		mIvClose = (ImageView) view.findViewById(R.id.dialog_input_password_close);
		mEt1 = (TextView) view.findViewById(R.id.dialog_input_password_1);
		mEt2 = (TextView) view.findViewById(R.id.dialog_input_password_2);
		mEt3 = (TextView) view.findViewById(R.id.dialog_input_password_3);
		mEt4 = (TextView) view.findViewById(R.id.dialog_input_password_4);
		mEt5 = (TextView) view.findViewById(R.id.dialog_input_password_5);
		mEt6 = (TextView) view.findViewById(R.id.dialog_input_password_6);
		mEt7 = (EditText) view.findViewById(R.id.dialog_input_password_7);
		mMoney = (TextView) view.findViewById(R.id.dialog_input_password_money);
		mTitle = (TextView) view.findViewById(R.id.dialog_input_password_title);
			
		mMoney.setText(money);
		mTitle.setText(title);
		if (type) {
			mEt1.setInputType(InputType.TYPE_CLASS_NUMBER);
			mEt2.setInputType(InputType.TYPE_CLASS_NUMBER);
			mEt3.setInputType(InputType.TYPE_CLASS_NUMBER);
			mEt4.setInputType(InputType.TYPE_CLASS_NUMBER);
			mEt5.setInputType(InputType.TYPE_CLASS_NUMBER);
			mEt6.setInputType(InputType.TYPE_CLASS_NUMBER);
			mEt7.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		mEt7.setFocusable(true);  
		mEt7.setFocusableInTouchMode(true);  
         //请求获得焦点  
		mEt7.requestFocus();  
         //调用系统输入法  
		mEt7.setFocusable(true);
		mEt7.setFocusableInTouchMode(true);
		mEt7.requestFocus();

		
		Timer timer = new Timer();

		timer.schedule(new TimerTask()

		{

			public void run()

			{

				InputMethodManager inputManager =

				(InputMethodManager) mEt7.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);

				inputManager.showSoftInput(mEt7, 0);

			}

		},

		998);
		
		
		mIvClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// finish();
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

		mEt7.setOnKeyListener(this);
		mEt7.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				String text = arg0.toString();
				if (text.length() == length) {

				} else {
					switch (text.length()) {
						case 1 :
							mEt1.setText("*");
							length = length;
							break;
						case 2 :
							mEt2.setText("*");
							length = length;
							break;
						case 3 :
							mEt3.setText("*");
							length = length;
							break;
						case 4 :
							mEt4.setText("*");
							length = length;
							break;
						case 5 :
							mEt5.setText("*");
							length = length;
							break;
						case 6 :
							mEt6.setText("*");
							length = length;

							dialog.dismiss();
							i.passwordSucceed(text);
							break;

						default :
							break;
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		return view;
	}

	private View returnViewPassWordError(int dialogInputPassword, String type, String mark, final OnClickListener i) {
		View view = View.inflate(mContext, dialogInputPassword, null);
		TextView content = (TextView) view.findViewById(R.id.password_error_tv_content);
		TextView cancel = (TextView) view.findViewById(R.id.password_error_tv_cancel);
		ImageView xian = (ImageView) view.findViewById(R.id.password_error_iv_xian);
		TextView forgetPassword = (TextView) view.findViewById(R.id.password_error_tv_forgetpassword);
		content.setText(mark);
//				09081=提现帐号非法
//				09082=系统错误请重新提现
//				09083=网路繁忙,提现失败,请重新申请
//				09084=连接出错
//				09085=无此账户
//				09086=钱包账户未激活
//				09087=钱包账户被冻结
//				09088=提现金额超过账户余额
//				09089=当天提现以超额
//				09090=当天提现次数以超出
//				09091=密码错误，你还可以输入{0}次
//				09092=验证码不正确
//				09093=登录密码已锁定，建议你找回密码，或{0}小时后重试
//				09094=少于最小提现金额
		
		if("09087".equals(type)||"09093".equals(type)){
			//取消  忘记密码
			cancel.setText("取消");
//			09087=钱包账户被冻结	
//			09093=登录密码已锁定，建议你找回密码，或{0}小时后重试
		}else if("09081".equals(type)||"09085".equals(type)||"09086".equals(type)||"09089".equals(type)||"09090".equals(type)||"09094".equals(type)||"09088".equals(type)){
			//取消
			forgetPassword.setVisibility(View.GONE);
			xian.setVisibility(View.GONE);
//			09081=提现帐号非法
//			09085=无此账户
//			09086=钱包账户未激活
//			09089=当天提现以超额
//			09090=当天提现次数以超出
//			09094=少于最小提现金额
//			09088=提现金额超过账户余额
		}else if("09082".equals(type)||"09083".equals(type)||"09084".equals(type)||"09092".equals(type)){
			//重新输入  取消
			forgetPassword.setText("重新输入");
//			09082=系统错误请重新提现
//			09083=网路繁忙,提现失败,请重新申请			
//			09084=连接出错	

//			09092=验证码不正确

		}else if("09091".equals(type)){
			//重新输入  忘记密码
			cancel.setText("重新输入");
//			09091=密码错误，你还可以输入{0}次			
			
			
		}
		
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog != null) {
					dialog.dismiss();
				}
				i.cancel();
			}
		});
		if(forgetPassword!=null){
			forgetPassword.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (dialog != null) {
						dialog.dismiss();
					}
					i.forgetPassword();
				}
			});
		}
		

		return view;
	}

	private static Builder ad;
	private static Dialog dialog;

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent arg2) {
		if (TYPE == IMPORTPASSWORD) {
			if (keyCode == KeyEvent.KEYCODE_DEL) {

				mEt1.setText("");
				mEt2.setText("");
				mEt3.setText("");
				mEt4.setText("");
				mEt5.setText("");
				mEt6.setText("");
				mEt7.setText("");
				length = 0;

			}

		}
		return false;
	}
	
	/**
	 * 显示错误两个按钮钱包的对话框。
	 * @param context
	 * @param content
	 * @param type
	 * @param i
	 */
	public static void showErrorTwoDialog(Context context, String content,String type , GeneralDialog.TwoButtonListener i){
		String strLeft="";
		String strRight="";
		
		if("09087".equals(type)||"09093".equals(type)){
			//取消  忘记密码
//			09087=钱包账户被冻结	
//			09093=登录密码已锁定，建议你找回密码，或{0}小时后重试
			strLeft="取消";
			strRight="忘记密码";
		}else if("09082".equals(type)||"09083".equals(type)||"09084".equals(type)||"09092".equals(type)||"02005".equals(type)){
			//重新输入  取消
//			09082=系统错误请重新提现
//			09083=网路繁忙,提现失败,请重新申请			
//			09084=连接出错	

//			09092=验证码不正确
			strLeft="取消";
			strRight="重新输入";
		}else if("09091".equals(type)){
			//重新输入  忘记密码
			strLeft="忘记密码";
			strRight="重新输入";
//			09091=密码错误，你还可以输入{0}次			
			
			
		}else{
			
			strLeft="取消";
			strRight="确定";
		}
		
		GeneralDialog.showTwoButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_1, "", content, strLeft, strRight, i);
	}
	/**
	 * 显示错误一个按钮钱包的对话框。
	 * @param context
	 * @param content
	 * @param type
	 * @param i
	 */
	public static void showErrorOneDialog(Context context, String content, GeneralDialog.OneButtonListener i){
//		09081=提现帐号非法
//		09085=无此账户
//		09086=钱包账户未激活
//		09089=当天提现以超额
//		09090=当天提现次数以超出
//		09094=少于最小提现金额
//		09088=提现金额超过账户余额
		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_2, "", content, "确定", i);
	}
	public interface PasswordListener {
		void passwordSucceed(String passWord);
	}

	private static void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
	public interface OnClickListener {
		void cancel();
		void forgetPassword();
	}
}
