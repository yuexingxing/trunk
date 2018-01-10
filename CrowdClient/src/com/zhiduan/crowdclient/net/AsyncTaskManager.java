package com.zhiduan.crowdclient.net;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.zhiduan.crowdclient.util.Logs;

import android.os.Handler;
import android.os.Message;


public class AsyncTaskManager
{
	private static final String TAG = "AsyncTaskManager";

	private static final int CORE_POOL_SIZE_TEXT = 5;
	private static final int CORE_POOL_SIZE_PIC = 5;
	private static final int CORE_POOL_SIZE_FILE = 1;
	private static final int MAXIMUM_POOL_SIZE = 128;
	private static final int KEEP_ALIVE = 10;

	private static final EnumMap<AsyncNetTask.TaskType, ThreadPoolExecutor> sExecutors = new EnumMap<AsyncNetTask.TaskType, ThreadPoolExecutor>(AsyncNetTask.TaskType.class);
	private static final EnumMap<AsyncNetTask.TaskType, LinkedList<AsyncNetTask>> sTasks = new EnumMap<AsyncNetTask.TaskType, LinkedList<AsyncNetTask>>(AsyncNetTask.TaskType.class);
	private static final HashMap<Integer, AsyncNetTask> sKeyToTaskMap = new HashMap<Integer, AsyncNetTask>();
	private static final HashMap<AsyncNetTask, Integer> sTaskToKeyMap = new HashMap<AsyncNetTask, Integer>();

	private static final ThreadFactory sThreadFactory = new ThreadFactory()
	{
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r)
		{
			return new Thread(r, "AsyncTaskBase #" + mCount.getAndIncrement());
		}
	};

	static
	{
		Logs.v(TAG, "AsyncTaskManager");

		sExecutors.put(AsyncNetTask.TaskType.GET_TEXT, new ThreadPoolExecutor(CORE_POOL_SIZE_TEXT, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE), sThreadFactory));
		sExecutors.put(AsyncNetTask.TaskType.GET_PIC,  new ThreadPoolExecutor(CORE_POOL_SIZE_PIC, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE), sThreadFactory));
	
		sExecutors.put(AsyncNetTask.TaskType.GET_FILE, new ThreadPoolExecutor(CORE_POOL_SIZE_FILE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE), sThreadFactory));
		

		sTasks.put(AsyncNetTask.TaskType.GET_TEXT, new LinkedList<AsyncNetTask>());
		sTasks.put(AsyncNetTask.TaskType.GET_PIC, new LinkedList<AsyncNetTask>());
		
		sTasks.put(AsyncNetTask.TaskType.GET_FILE, new LinkedList<AsyncNetTask>());
	}

	public static AsyncNetTask createAsyncTask(AsyncNetTask.TaskType type, NetTaskParams params)
	{
		int key = params.getKey();

		if ((key != 0) && sKeyToTaskMap.containsKey(key))
			return sKeyToTaskMap.get(key);

		AsyncNetTask task = null;

		switch (type)
		{
			case GET_TEXT:
				task = new LoadTextNetTask(sExecutors.get(type), (LoadTextParams) params);
				break;
				
			case GET_PIC:
				task = new LoadPicNetTask(sExecutors.get(type), (LoadPicParams) params);
				break;
				
			case GET_FILE:
//				task = new LoadFileNetTask(sExecutors.get(type), (LoadFileParams) params);
				break;			
				
			default:
				task = null;
				break;
		}

		if (task != null)
		{
			sTasks.get(type).offer(task);

			if (key != 0)
			{
				sKeyToTaskMap.put(key, task);
				sTaskToKeyMap.put(task, key);
			}
		}
		
		return task;
	}

	private static boolean inParamsSet(Set<NetTaskParams> set, NetTaskParams params)
	{
		if (set.contains(params))
			return true;
		for (NetTaskParams p : set)
			if (p.equals(params))
				return true;
		return false;
	}

	public static void removeTask(AsyncNetTask task, Set<NetTaskParams> exKeys)
	{
		if ((exKeys != null) && inParamsSet(exKeys, task.getParams()))
			return;

		if (sTaskToKeyMap.containsKey(task))
		{
			int key = sTaskToKeyMap.get(task);
			sTaskToKeyMap.remove(task);
			sKeyToTaskMap.remove(key);
		}

		sTasks.get(task.getType()).remove(task);
	}

	// 杩唬鏃朵娇鐢?
	private static void removeTask(AsyncNetTask task, Set<NetTaskParams> exKeys, ListIterator<AsyncNetTask> it)
	{
		if ((exKeys != null) && inParamsSet(exKeys, task.getParams()))
			return;

		if (sTaskToKeyMap.containsKey(task))
		{
			int key = sTaskToKeyMap.get(task);
			sTaskToKeyMap.remove(task);
			sKeyToTaskMap.remove(key);
		}

		it.remove();
	}

	public static void cancelTasks(AsyncNetTask.TaskType type, AsyncTaskBase.Status status, Set<NetTaskParams> exKeys, boolean mayInterruptIfRunning)
	{
		ListIterator<AsyncNetTask> it = sTasks.get(type).listIterator();
		while (it.hasNext())
		{
			AsyncNetTask task = it.next();

			if ((exKeys != null) && inParamsSet(exKeys, task.getParams()))
				continue;

			if (task.getStatus() == status)
			{
				task.cancel(mayInterruptIfRunning);
				removeTask(task, null, it);
			}
		}
	}

	private static void cancelTasks(boolean allType, AsyncNetTask.TaskType type, boolean allStatus, AsyncTaskBase.Status status, Set<NetTaskParams> exKeys, boolean mayInterruptIfRunning)
	{
		if (allType)
		{
			for (AsyncNetTask.TaskType t : sTasks.keySet())
			{
				if (allStatus)
				{
					cancelTasks(t, AsyncTaskBase.Status.PENDING, exKeys, mayInterruptIfRunning);
					cancelTasks(t, AsyncTaskBase.Status.WAITING, exKeys, mayInterruptIfRunning);
					cancelTasks(t, AsyncTaskBase.Status.RUNNING, exKeys, mayInterruptIfRunning);
					cancelTasks(t, AsyncTaskBase.Status.FINISHED, exKeys, mayInterruptIfRunning);
				}
				else
				{
					cancelTasks(t, status, exKeys, mayInterruptIfRunning);
				}
			}
		}
		else
		{
			if (allStatus)
			{
				cancelTasks(type, AsyncTaskBase.Status.PENDING, exKeys, mayInterruptIfRunning);
				cancelTasks(type, AsyncTaskBase.Status.WAITING, exKeys, mayInterruptIfRunning);
				cancelTasks(type, AsyncTaskBase.Status.RUNNING, exKeys, mayInterruptIfRunning);
				cancelTasks(type, AsyncTaskBase.Status.FINISHED, exKeys, mayInterruptIfRunning);
			}
			else
			{
				cancelTasks(type, status, exKeys, mayInterruptIfRunning);
			}
		}
	}

	public static void cancelTasks(AsyncNetTask.TaskType type, Set<NetTaskParams> exKeys, boolean mayInterruptIfRunning)
	{
		cancelTasks(false, type, true, AsyncTaskBase.Status.PENDING, exKeys, mayInterruptIfRunning);
	}

	public static void cancelTasks(AsyncTaskBase.Status status, Set<NetTaskParams> exKeys, boolean mayInterruptIfRunning)
	{
		cancelTasks(true, AsyncNetTask.TaskType.GET_TEXT, false, status, exKeys, mayInterruptIfRunning);
	}

	public static void cancelTasks(Set<NetTaskParams> exKeys, boolean mayInterruptIfRunning)
	{
		cancelTasks(true, AsyncNetTask.TaskType.GET_TEXT, true, AsyncTaskBase.Status.PENDING, exKeys, mayInterruptIfRunning);
	}

	public static void cancelTask(AsyncTaskBase.Status status, AsyncNetTask task, Set<NetTaskParams> exKeys, boolean mayInterruptIfRunning)
	{
		if ((exKeys != null) && inParamsSet(exKeys, task.getParams()))
			return;

		if (task.getStatus() == status)
		{
			task.cancel(mayInterruptIfRunning);
			removeTask(task, exKeys);
		}
	}

	public static void cancelTask(AsyncNetTask task, Set<NetTaskParams> exKeys, boolean mayInterruptIfRunning)
	{
		if ((exKeys != null) && inParamsSet(exKeys, task.getParams()))
			return;

		task.cancel(mayInterruptIfRunning);
		removeTask(task, exKeys);
	}

	// 鍒涘缓鍚庡伐浣滅嚎绋嬩腑浼氳璋冪敤锛屼笉闇?繚鎶わ紝鍥犱负宸ヤ綔绾跨▼涓彧璇诲彇鏁版嵁
	public static int getTaskCount(AsyncNetTask.TaskType type)
	{
		return sTasks.get(type).size();
	}

	public static int getTaskCount(AsyncNetTask.TaskType type, AsyncTaskBase.Status status)
	{
		int count = 0;
		ListIterator<AsyncNetTask> it = sTasks.get(type).listIterator();
		while (it.hasNext())
		{
			AsyncNetTask task = it.next();
			if (task.getStatus() == status)
				count++;
		}
		return count;
	}

	public static int getTaskCount(AsyncTaskBase.Status status)
	{
		int count = 0;
		count += getTaskCount(AsyncNetTask.TaskType.GET_TEXT, status);
		count += getTaskCount(AsyncNetTask.TaskType.GET_PIC, status);
		count += getTaskCount(AsyncNetTask.TaskType.GET_FILE, status);
		return count;
	}

	// 鍒涘缓鍚庡伐浣滅嚎绋嬩腑浼氳璋冪敤锛屽唴閮ㄤ細璋冪敤getTaskCount()
	public static void yieldIfNeeded(AsyncNetTask me)
	{
		if (me.getType() == AsyncNetTask.TaskType.GET_PIC)
		{
			while (getTaskCount(AsyncNetTask.TaskType.GET_TEXT) != 0)
			{
				try
				{
					Thread.sleep(200);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
