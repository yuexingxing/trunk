package com.zhiduan.crowdclient.photoalbum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class PhotoAlbumReceiver extends BroadcastReceiver {

	private final Handler handler;

	public PhotoAlbumReceiver(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onReceive(Context arg0, Intent intent) {
		if (intent == null)
			return;
		String path = intent.getStringExtra("path");
		Message msg = new Message();
		msg.obj = path;
		handler.sendMessage(msg);
	}
}
