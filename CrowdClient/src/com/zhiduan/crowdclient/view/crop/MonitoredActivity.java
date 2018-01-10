/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhiduan.crowdclient.view.crop;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import com.zhiduan.crowdclient.util.ScreenUtil;

/*
 * Modified from original in AOSP.
 */
abstract class MonitoredActivity extends Activity {

    private final ArrayList<LifeCycleListener> listeners = new ArrayList<LifeCycleListener>();

    public static interface LifeCycleListener {
        public void onActivityCreated(MonitoredActivity activity);
        public void onActivityDestroyed(MonitoredActivity activity);
        public void onActivityStarted(MonitoredActivity activity);
        public void onActivityStopped(MonitoredActivity activity);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onActivityCreated(MonitoredActivity activity) {}
        public void onActivityDestroyed(MonitoredActivity activity) {}
        public void onActivityStarted(MonitoredActivity activity) {}
        public void onActivityStopped(MonitoredActivity activity) {}
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        listeners.remove(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (LifeCycleListener listener : listeners) {
            listener.onActivityCreated(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (LifeCycleListener listener : listeners) {
            listener.onActivityDestroyed(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (LifeCycleListener listener : listeners) {
            listener.onActivityStarted(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (LifeCycleListener listener : listeners) {
            listener.onActivityStopped(this);
        }
    }

    public void setImmerseLayout(View view) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			/*
			 * window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			 * , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			 */
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}else{
			view.setVisibility(View.GONE);
		}
	}
}
