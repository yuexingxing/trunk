package com.zhiduan.crowdclient.net;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author HeXiuHui
 *
 */
public abstract class AsyncNetTask extends AsyncTaskBase<NetTaskParams, Long, NetTaskResult>
{
	public enum TaskType
	{
		GET_TEXT, GET_PIC, GET_FILE
	};

	protected TaskType mType;
	protected NetTaskParams mParams;

	protected ArrayList<OnPreExecuteListener> mOnPreExecuteListeners;
	protected ArrayList<OnPostExecuteListener> mOnPostExecuteListeners;
	protected ArrayList<OnProgressUpdateListener> mOnProgressUpdateListeners;
	protected ArrayList<OnTaskCancelledListener> mOnTaskCancelledListeners;
	protected ArrayList<Object> mOnPreExecuteListenerTags;
	protected ArrayList<Object> mOnPostExecuteListenerTags;
	protected ArrayList<Object> mOnProgressUpdateListenerTags;
	protected ArrayList<Object> mOnTaskCancelledListenerTags;

	public AsyncNetTask(ThreadPoolExecutor executor, NetTaskParams params)
	{
		super(executor);
		mParams = params;
	}

	protected void setType(TaskType type)
	{
		mType = type;
	}

	public TaskType getType()
	{
		return mType;
	}

	public NetTaskParams getParams()
	{
		return mParams;
	}

	public interface OnPreExecuteListener
	{
		void onPreExecute(AsyncNetTask t, Object tag);
	}

	public interface OnPostExecuteListener
	{
		void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag);
	}

	public interface OnProgressUpdateListener
	{
		void onProgressUpdate(AsyncNetTask t, Object tag, Long... values);
	}

	public interface OnTaskCancelledListener
	{
		void onCancelled(AsyncNetTask t, Object tag);
	}

	public void addOnPreExecuteListener(OnPreExecuteListener l, Object tag)
	{
		if (mOnPreExecuteListeners == null)
		{
			mOnPreExecuteListeners = new ArrayList<OnPreExecuteListener>();
		}
		mOnPreExecuteListeners.add(l);

		if (mOnPreExecuteListenerTags == null)
		{
			mOnPreExecuteListenerTags = new ArrayList<Object>();
		}
		mOnPreExecuteListenerTags.add(tag);
	}

	public void removeOnPreExecuteListener(OnPreExecuteListener l)
	{
		if (mOnPreExecuteListeners == null)
		{
			return;
		}

		int i = mOnPreExecuteListeners.indexOf(l);
		if (i < 0)
		{
			return;
		}
		mOnPreExecuteListeners.remove(i);

		if (mOnPreExecuteListenerTags == null)
		{
			return;
		}
		mOnPreExecuteListenerTags.remove(i);
	}

	public void clearOnPreExecuteListener()
	{
		if (mOnPreExecuteListeners != null)
		{
			mOnPreExecuteListeners.clear();
		}

		if (mOnPreExecuteListenerTags != null)
		{
			mOnPreExecuteListenerTags.clear();
		}
	}

	public void addOnPostExecuteListener(OnPostExecuteListener l, Object tag)
	{
		if (mOnPostExecuteListeners == null)
		{
			mOnPostExecuteListeners = new ArrayList<OnPostExecuteListener>();
		}
		mOnPostExecuteListeners.add(l);

		if (mOnPostExecuteListenerTags == null)
		{
			mOnPostExecuteListenerTags = new ArrayList<Object>();
		}
		mOnPostExecuteListenerTags.add(tag);
	}

	public void removeOnPostExecuteListener(OnPostExecuteListener l)
	{
		if (mOnPostExecuteListeners == null)
		{
			return;
		}

		int i = mOnPostExecuteListeners.indexOf(l);
		if (i < 0)
		{
			return;
		}

		mOnPostExecuteListeners.remove(i);

		if (mOnPostExecuteListenerTags == null)
		{
			return;
		}

		mOnPostExecuteListenerTags.remove(i);
	}

	public void clearOnPostExecuteListener()
	{
		if (mOnPostExecuteListeners != null)
		{
			mOnPostExecuteListeners.clear();
		}

		if (mOnPostExecuteListenerTags != null)
		{
			mOnPostExecuteListenerTags.clear();
		}
	}

	public void addOnProgressUpdateListener(OnProgressUpdateListener l, Object tag)
	{
		if (mOnProgressUpdateListeners == null)
		{
			mOnProgressUpdateListeners = new ArrayList<OnProgressUpdateListener>();
		}
		mOnProgressUpdateListeners.add(l);

		if (mOnProgressUpdateListenerTags == null)
		{
			mOnProgressUpdateListenerTags = new ArrayList<Object>();
		}
		mOnProgressUpdateListenerTags.add(tag);
	}

	public void removeOnProgressUpdateListener(OnProgressUpdateListener l)
	{
		if (mOnProgressUpdateListeners == null)
		{
			return;
		}

		int i = mOnProgressUpdateListeners.indexOf(l);
		if (i < 0)
		{
			return;
		}

		mOnProgressUpdateListeners.remove(i);

		if (mOnProgressUpdateListenerTags == null)
		{
			return;
		}

		mOnProgressUpdateListenerTags.remove(i);
	}

	public void clearOnProgressUpdateListener()
	{
		if (mOnProgressUpdateListeners != null)
		{
			mOnProgressUpdateListeners.clear();
		}

		if (mOnProgressUpdateListenerTags != null)
		{
			mOnProgressUpdateListenerTags.clear();
		}
	}

	public void addOnTaskCancelledListener(OnTaskCancelledListener l, Object tag)
	{
		if (mOnTaskCancelledListeners == null)
		{
			mOnTaskCancelledListeners = new ArrayList<OnTaskCancelledListener>();
		}
		mOnTaskCancelledListeners.add(l);

		if (mOnTaskCancelledListenerTags == null)
		{
			mOnTaskCancelledListenerTags = new ArrayList<Object>();
		}
		mOnTaskCancelledListenerTags.add(tag);
	}

	public void removeOnTaskCancelledListener(OnTaskCancelledListener l)
	{
		if (mOnTaskCancelledListeners == null)
		{
			return;
		}

		int i = mOnTaskCancelledListeners.indexOf(l);
		if (i < 0)
		{
			return;
		}

		mOnTaskCancelledListeners.remove(i);
		if (mOnTaskCancelledListenerTags == null)
		{
			return;
		}
		mOnTaskCancelledListenerTags.remove(i);
	}

	public void clearOnTaskCancelledListener()
	{
		if (mOnTaskCancelledListeners != null)
		{
			mOnTaskCancelledListeners.clear();
		}

		if (mOnTaskCancelledListenerTags != null)
		{
			mOnTaskCancelledListenerTags.clear();
		}
	}

	public void clearAllListener()
	{
		clearOnPreExecuteListener();
		clearOnPostExecuteListener();
		clearOnProgressUpdateListener();
		clearOnTaskCancelledListener();
	}

	/*
	 * doInBackground() params[2]鐠囧瓨妲慙ong Long Long flag total curr
	 * status 0 n/a n/a yield 1 recommendLen(ResourceDownloaderParams)
	 * fileLen(local) preConn 0(NetTaskParams else) 0 2 totalLen(server) currLen
	 * pstConn
	 */

	@Override
	// protected abstract Result doInBackground(Params... params)
	protected abstract NetTaskResult doInBackground(NetTaskParams... params);

	@Override
	protected void onPreExecute()
	{
		if (mOnPreExecuteListeners == null)
		{
			return;
		}

		for (int i = 0; i < mOnPreExecuteListeners.size(); i++)
		{
			OnPreExecuteListener l = mOnPreExecuteListeners.get(i);
			Object tag = null;
			if (mOnPreExecuteListenerTags != null)
			{
				tag = mOnPreExecuteListenerTags.get(i);
			}
	
			l.onPreExecute(this, tag);
		}
	}

	@Override
	// protected void onPostExecute(Result result)
	protected void onPostExecute(NetTaskResult result)
	{
		if (mOnPostExecuteListeners == null)
		{
			return;
		}

		for (int i = 0; i < mOnPostExecuteListeners.size(); i++)
		{
			OnPostExecuteListener l = mOnPostExecuteListeners.get(i);
			Object tag = null;
			if (mOnPostExecuteListenerTags != null)
			{
				tag = mOnPostExecuteListenerTags.get(i);
			}

			l.onPostExecute(this, result, tag);
		}

		AsyncTaskManager.removeTask(this, null);
	}

	@Override
	// protected void onProgressUpdate(Progress... values)
	protected void onProgressUpdate(Long... values)
	{
		if (mOnProgressUpdateListeners == null)
		{
			return;
		}

		for (int i = 0; i < mOnProgressUpdateListeners.size(); i++)
		{
			OnProgressUpdateListener l = mOnProgressUpdateListeners.get(i);
			Object tag = null;
			if (mOnProgressUpdateListenerTags != null)
			{
				tag = mOnProgressUpdateListenerTags.get(i);
			}
			l.onProgressUpdate(this, tag, values);
		}
	}

	@Override
	protected void onCancelled()
	{
		AsyncTaskManager.removeTask(this, null);

		if (mOnTaskCancelledListeners == null)
		{
			return;
		}

		for (int i = 0; i < mOnTaskCancelledListeners.size(); i++)
		{
			OnTaskCancelledListener l = mOnTaskCancelledListeners.get(i);
			Object tag = null;
			if (mOnTaskCancelledListeners != null)
			{
				tag = mOnTaskCancelledListenerTags.get(i);
			}
			l.onCancelled(this, tag);
		}

		//AsyncTaskManager.removeTask(this, null);
	}

	public void executeMe()
	{
		if (getStatus() == AsyncTaskBase.Status.PENDING)
		{
			execute(mParams);
		}
	}
}
