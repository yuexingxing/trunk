/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhiduan.crowdclient.im;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;
import com.zhiduan.crowdclient.R;

@SuppressLint("NewApi")
public class ImMainActivity extends ImBaseActivity {

	protected static final String TAG = "MainActivity";
	// textview for unread message count
//	private TextView unreadLabel;
	// textview for unread event message
//	private TextView unreadAddressLable;

//	private Button[] mTabs;
	private ContactListFragment contactListFragment;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	// user logged into another device
	public boolean isConflict = false;
	// user account was removed
	private boolean isCurrentAccountRemoved = false;
	

	/**
	 * check if current user account was remove
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//		    String packageName = getPackageName();
//		    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
////		    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
////		        Intent intent = new Intent();
////		        intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
////		        intent.setData(Uri.parse("package:" + packageName));
////		        startActivity(intent);
////		    }
//		}
		
		//make sure activity will not in background if user is logged into another device or removed
		if (savedInstanceState != null && savedInstanceState.getBoolean(ImConstant.ACCOUNT_REMOVED, false)) {
		    DemoHelper.getInstance().logout(false,null);
			finish();
			startActivity(new Intent(this, ImLoginActivity.class));
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			finish();
			startActivity(new Intent(this, ImLoginActivity.class));
			return;
		}
		setContentView(R.layout.em_activity_main);
		// runtime permission for android 6.0, just require all permissions here for simple
		requestPermissions();

		initView();
		setImmerseLayout();

		Log.i("hexiuhui----", "onCreateonCreateonCreateonCreate");
		
		showExceptionDialogFromIntent(getIntent());

		inviteMessgeDao = new InviteMessgeDao(this);
		UserDao userDao = new UserDao(this);
		conversationListFragment = new ConversationListFragment();
		contactListFragment = new ContactListFragment();
		fragments = new Fragment[] { conversationListFragment, contactListFragment};

		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
				.add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
				.commit();

		//register broadcast receiver to receive the change of group from DemoHelper
		registerBroadcastReceiver();
		
		
		EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
		//debug purpose only
        registerInternalDebugReceiver();
	}
	
	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	@TargetApi(23)
	private void requestPermissions() {
		PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
			@Override
			public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDenied(String permission) {
				//Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * init views
	 */
	private void initView() {
//		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
//		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
//		mTabs = new Button[2];
//		mTabs[0] = (Button) findViewById(R.id.btn_conversation);
//		mTabs[1] = (Button) findViewById(R.id.btn_address_list);
		// select first tab
//		mTabs[0].setSelected(true);
	}

	/**
	 * on tab clicked
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
//		if (view.getId() == R.id.btn_conversation) {
//			index = 0;
//		} else if (view.getId() == R.id.btn_address_list) {
//			index = 1;
//		}
		
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
//		mTabs[currentTabIndex].setSelected(false);
//		// set current tab selected
//		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

EMMessageListener messageListener = new EMMessageListener() {
		
		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			// notify new message
		    for (EMMessage message : messages) {
		        DemoHelper.getInstance().getNotifier().onNewMsg(message);
		    }
			refreshUIWithMessage();
		}
		
		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
//			//red packet code : 处理红包回执透传消息
//			for (EMMessage message : messages) {
//				EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
//				final String action = cmdMsgBody.action();//获取自定义action
//				if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
//					RedPacketUtil.receiveRedPacketAckMessage(message);
//				}
//			}
			//end of red packet code
			refreshUIWithMessage();
		}
		
		@Override
		public void onMessageRead(List<EMMessage> messages) {
		}
		
		@Override
		public void onMessageDelivered(List<EMMessage> message) {
		}
		
		@Override
		public void onMessageChanged(EMMessage message, Object change) {}
	};

	private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// refresh unread count
				updateUnreadLabel();
				if (currentTabIndex == 0) {
					// refresh conversation list
					if (conversationListFragment != null) {
						conversationListFragment.refresh();
					}
				}
			}
		});
	}

//	@Override
//	public void back(View view) {
//		super.back(view);
//	}
	
	private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ImConstant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(ImConstant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
                updateUnreadAddressLable();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                } else if (currentTabIndex == 1) {
                    if(contactListFragment != null) {
                        contactListFragment.refresh();
                    }
                }
                String action = intent.getAction();
                if(action.equals(ImConstant.ACTION_GROUP_CHANAGED)){
//                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
                }
			}
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
	
	public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {}
        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
					if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
							username.equals(ChatActivity.activityInstance.toChatUsername)) {
					    String st10 = getResources().getString(R.string.have_you_removed);
					    Toast.makeText(ImMainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
					    .show();
					    ChatActivity.activityInstance.finish();
					}
                }
            });
	        updateUnreadAddressLable();
        }
        @Override
        public void onContactInvited(String username, String reason) {}
        @Override
        public void onFriendRequestAccepted(String username) {}
        @Override
        public void onFriendRequestDeclined(String username) {}
	}
	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		
		if (exceptionBuilder != null) {
		    exceptionBuilder.create().dismiss();
		    exceptionBuilder = null;
		    isExceptionDialogShow = false;
		}
		unregisterBroadcastReceiver();

		try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
	}

	/**
	 * update unread message count
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
//		if (count > 0) {
//			unreadLabel.setText(String.valueOf(count));
//			unreadLabel.setVisibility(View.VISIBLE);
//		} else {
//			unreadLabel.setVisibility(View.INVISIBLE);
//		}
	}

	/**
	 * update the total unread count 
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
//				if (count > 0) {
//					unreadAddressLable.setVisibility(View.VISIBLE);
//				} else {
//					unreadAddressLable.setVisibility(View.INVISIBLE);
//				}
			}
		});

	}

	/**
	 * get unread event notification count, including application, accepted, etc
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
		return unreadAddressCountTotal;
	}

	/**
	 * get unread message count
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
		for(EMConversation conversation:EMClient.getInstance().chatManager().getAllConversations().values()){
			if(conversation.getType() == EMConversationType.ChatRoom)
			chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}

	private InviteMessgeDao inviteMessgeDao;

	@Override
	protected void onResume() {
		super.onResume();
		
		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
		}

		// unregister this event listener when this activity enters the
		// background
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.pushActivity(this);

		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

	@Override
	protected void onStop() {
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(ImConstant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			moveTaskToBack(false);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	private android.app.AlertDialog.Builder exceptionBuilder;
	private boolean isExceptionDialogShow =  false;
    private BroadcastReceiver internalDebugReceiver;
    private ConversationListFragment conversationListFragment;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;

    private int getExceptionMessageId(String exceptionType) {
         if(exceptionType.equals(ImConstant.ACCOUNT_CONFLICT)) {
             return R.string.connect_conflict;
         } else if (exceptionType.equals(ImConstant.ACCOUNT_REMOVED)) {
             return R.string.em_user_remove;
         } else if (exceptionType.equals(ImConstant.ACCOUNT_FORBIDDEN)) {
             return R.string.user_forbidden;
         }
         return R.string.Network_error;
    }
	/**
	 * show the dialog when user met some exception: such as login on another device, user removed or user forbidden
	 */
	private void showExceptionDialog(String exceptionType) {
	    isExceptionDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!ImMainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (exceptionBuilder == null)
				    exceptionBuilder = new android.app.AlertDialog.Builder(ImMainActivity.this);
				    exceptionBuilder.setTitle(st);
				    exceptionBuilder.setMessage(getExceptionMessageId(exceptionType));	
				    exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						exceptionBuilder = null;
						isExceptionDialogShow = false;
						finish();
						Intent intent = new Intent(ImMainActivity.this, ImLoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
				exceptionBuilder.setCancelable(false);
				exceptionBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}
		}
	}

	private void showExceptionDialogFromIntent(Intent intent) {
	    EMLog.e(TAG, "showExceptionDialogFromIntent");
	    if (!isExceptionDialogShow && intent.getBooleanExtra(ImConstant.ACCOUNT_CONFLICT, false)) {
//            showExceptionDialog(ImConstant.ACCOUNT_CONFLICT);
//            DemoHelper.getInstance().logout(false,null);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(ImConstant.ACCOUNT_REMOVED, false)) {
            showExceptionDialog(ImConstant.ACCOUNT_REMOVED);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(ImConstant.ACCOUNT_FORBIDDEN, false)) {
            showExceptionDialog(ImConstant.ACCOUNT_FORBIDDEN);
        }   
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i("hexiuhui---", "onNewIntentonNewIntentonNewIntentonNewIntent");
		showExceptionDialogFromIntent(intent);
	}
	
	/**
	 * debug purpose only, you can ignore this
	 */
	private void registerInternalDebugReceiver() {
	    internalDebugReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(false,new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                finish();
                                startActivity(new Intent(ImMainActivity.this, ImLoginActivity.class));
                            }
                        });
                    }
                    
                    @Override
                    public void onProgress(int progress, String status) {}
                    
                    @Override
                    public void onError(int code, String message) {}
                });
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }

	@Override 
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
	}
}
