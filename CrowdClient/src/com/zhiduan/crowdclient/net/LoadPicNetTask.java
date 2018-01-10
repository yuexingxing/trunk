package com.zhiduan.crowdclient.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import com.zhiduan.crowdclient.util.Logs;

import android.graphics.BitmapFactory;

/**
 * @author HeXiuHui
 *
 */
public class LoadPicNetTask extends AsyncNetTask
{
	public LoadPicNetTask(ThreadPoolExecutor executor, LoadPicParams params)
	{
		super(executor, params);
		setType(AsyncNetTask.TaskType.GET_PIC);
	}

//	protected NetTaskResult doFakeTaskWork(NetTaskParams... params)
//	{
//		LoadPicResult result = new LoadPicResult();
//
//		NetTaskUtil.doFakeTaskWork(this, params);
//		result.m_nResultCode = 0;
//		result.m_strResultDesc = "don";
//		return result;
//	}

	protected NetTaskResult doLoadPicWork(NetTaskParams... params)
	{
		final LoadPicParams lpp = (LoadPicParams) params[0];
		LoadPicResult pstResult = new LoadPicResult();
		FileOutputStream stream = null;
		
		try
		{
			LoadResult preResult = NetTaskUtil.doLoadWork(this, params);
			pstResult.m_nStatusCode = preResult.m_nStatusCode;
			
			if (preResult.m_nResultCode == 0)
			{
				if (lpp.m_bLoadBitmap)
				{
					pstResult.mBitmap = BitmapFactory.decodeByteArray(preResult.buf, 0, preResult.buf.length);
					
					Logs.v("Test", "bitmap = " + preResult.buf.length);
				}
				
				if (lpp.m_bWriteToFile)
				{
//					if(!GlobalData.bSdCardIsAccessable)
//					{
//						pstResult.m_nResultCode = -1;	
//						pstResult.m_strResultDesc = "SdCard can't access";
//						return pstResult;
//					}
					
					if (lpp.m_strFileName == null)
					{
						stream = new FileOutputStream(new File(lpp.getTargetFileName()));
					}
					else
					{
						stream = new FileOutputStream(new File(lpp.m_strFileName));
					}
					stream.write(preResult.buf);
				}
				pstResult.m_nResultCode = 0;
				pstResult.m_strResultDesc = "";
			}
			else
			{
				pstResult.m_nResultCode = preResult.m_nResultCode;
				pstResult.m_strResultDesc = preResult.m_strResultDesc;
			}
		}
		catch (OutOfMemoryError e)
		{
			e.printStackTrace();
			pstResult.m_nResultCode = -1;
			pstResult.m_strResultDesc = e.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			pstResult.m_nResultCode = -1;
			pstResult.m_strResultDesc = e.toString();
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return pstResult;
	}

	@Override
	protected NetTaskResult doInBackground(NetTaskParams... params)
	{
		publishProgress(0L);
		AsyncTaskManager.yieldIfNeeded(this);
		publishProgress(1L, 0L, 0L);
		return doLoadPicWork(params);
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(NetTaskResult result)
	{
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Long... values)
	{
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled()
	{
		super.onCancelled();
	}
}
