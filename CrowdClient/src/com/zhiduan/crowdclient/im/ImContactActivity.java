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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zhiduan.crowdclient.R;

@SuppressLint("NewApi")
public class ImContactActivity extends ImBaseActivity {

	protected static final String TAG = "MainActivity";
	private ContactListFragment contactListFragment;
	// user logged into another device
	public boolean isConflict = false;
	// user account was removed
	private boolean isCurrentAccountRemoved = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_friends);
		requestPermissions();

		initView();

		contactListFragment = new ContactListFragment();

		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_aa, contactListFragment).show(contactListFragment)
				.commit();

		setImmerseLayout();
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

	@Override
	public void back(View view) {
		super.back(view);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onStop() {

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


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
	

	@Override 
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
	}
}
