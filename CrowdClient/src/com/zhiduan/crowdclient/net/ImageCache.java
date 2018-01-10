package com.zhiduan.crowdclient.net;

import java.io.File;

import com.zhiduan.crowdclient.util.Logs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author HeXiuHui
 *
 */
public class ImageCache
{
    public static boolean contains(String strUrl)
    {
    	
        String fileName = getRecommendFileName(strUrl);
        
        Logs.i("hexiuhui-----", fileName + "llll");
        
        if (fileName == null)
        {
            return false;
        }
        
        File file = new File(fileName);
        
        Logs.i("hexiuhui-----", file.exists() + "dd");
        
        return file.exists();
    }
    
	public static String getRecommendFileName(String strUrl)
	{
		return NetUtil.getFileNameFromUrl(strUrl);
	}

    public static Bitmap get(String strUrl)
    {
        if (contains(strUrl))
        {
        	try
			{
        		 return BitmapFactory.decodeFile(getRecommendFileName(strUrl));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
        }
        else
        {
            return null;
        }
    }
 
	public static Bitmap get(String strUrl, int nWidth, int nHeight, boolean bKeepRatio)
	{
		if (!contains(strUrl))
		{
			return null;
		}

		if(nWidth == -1 && nHeight == -1)
		{
			return get(strUrl);
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inJustDecodeBounds = true;

		try
		{
			BitmapFactory.decodeFile(getRecommendFileName(strUrl), options);

			options.inJustDecodeBounds = false;

			int nOrgWidth = options.outWidth;
			int nOrgHeight = options.outHeight;
			
			if(nHeight == -1)
			{
				float fScale = 1;
				if(nOrgWidth > nWidth)
				{
					fScale = (float)nWidth / nOrgWidth;
				}
				nHeight = (int)(nOrgHeight * fScale + 0.5);
				
				if (nOrgWidth / nWidth >= 2)
				{
					int xScale = nOrgWidth / nWidth;
					int yScale = nOrgHeight / nHeight;
					int nMaxScale = xScale > yScale ? xScale : yScale;
	
					int nMaxSampleSize = 2;
					while (nMaxSampleSize * 2 < nMaxScale)
					{
						nMaxSampleSize *= 2;
					}
					
					options.inSampleSize = nMaxSampleSize;
				}						
			} 
			else if (nWidth == -1) 
			{
				float fScale = 1;
				if(nOrgHeight > nHeight)
				{
					fScale = (float)nHeight / nOrgHeight;
				}
				nWidth = (int)(nOrgWidth * fScale + 0.5);

				if (nOrgHeight / nHeight >= 2)
				{
					int xScale = nOrgWidth / nWidth;
					int yScale = nOrgHeight / nHeight;
					int nMaxScale = xScale > yScale ? xScale : yScale;
	
					int nMaxSampleSize = 2;
					while (nMaxSampleSize * 2 < nMaxScale)
					{
						nMaxSampleSize *= 2;
					}
					
					options.inSampleSize = nMaxSampleSize;
				}		
			}
			else
			{
				if (nOrgWidth / nWidth >= 2 || nOrgHeight / nHeight >= 2)
				{
					int xScale = nOrgWidth / nWidth;
					int yScale = nOrgHeight / nHeight;
					int nMaxScale = xScale > yScale ? xScale : yScale;
	
					int nMaxSampleSize = 2;
					while (nMaxSampleSize * 2 < nMaxScale)
					{
						nMaxSampleSize *= 2;
					}
					
					options.inSampleSize = nMaxSampleSize;
				}				
			}

			Bitmap bmpOrg = BitmapFactory.decodeFile(getRecommendFileName(strUrl), options);
			if (bmpOrg == null)
			{
				return null;
			}
			if (bmpOrg.getWidth() == nWidth && bmpOrg.getHeight() == nHeight)
			{
				return bmpOrg;
			}
			if(bKeepRatio)
			{
				nOrgWidth = bmpOrg.getWidth();
				nOrgHeight = bmpOrg.getHeight();
				
				if(nOrgWidth <= nWidth && nOrgHeight <= nHeight)
				{
					return bmpOrg;
				}
				
				float xScale = nWidth / (float)nOrgWidth;
				float yScale = nHeight / (float)nOrgHeight;
				float fScale = xScale < yScale ? xScale : yScale;

				nWidth = (int)(nOrgWidth * fScale);
				nHeight = (int)(nOrgHeight * fScale);
			}
			
			Bitmap bitmap = Bitmap.createScaledBitmap(bmpOrg, nWidth, nHeight, true);

			if (bmpOrg != bitmap && !bmpOrg.isRecycled())
			{
				bmpOrg.recycle();
				bmpOrg = null;
			}

			return bitmap;
		}
		catch (OutOfMemoryError e)
		{
			// System.gc();
			return null;
		}
		catch (Exception e)
		{
			// System.gc();
			return null;
		}
	}    
}    
