package com.zhiduan.crowdclient.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.Toast;

import com.zhiduan.crowdclient.R;

/**
 * 
 *
 * @author yxx
 *
 * 2015-12-3
 */
public class VoiceHint {
	private static SoundPool sp; // 声音变量
	private static HashMap<Integer, Integer> spMap; // 声音集合

	/**
	 *播放正确声音
	 * 
	 * @param number
	 */
	public static void playRightSounds() {

		int sound = 1; // 默认正确声音

		sp.play(spMap.get(sound), volumnRatio1, volumnRatio1, 1, 0, 1);
	}

	/**
	 * 播放错误声音
	 * 
	 * @param number
	 */
	public static void playErrorSounds() {

		int sound = 2; 

		sp.play(spMap.get(sound), volumnRatio1, volumnRatio1, 1, 0, 1);
	}

	/**
	 *播放新订单声音
	 * 
	 * @param number
	 */
	public static void playNewOrderSounds() {

		int sound = 3; // 默认正确声音

		sp.play(spMap.get(sound), volumnRatio1, volumnRatio1, 1, 0, 1);
	}

	private static AudioManager am = null;

	private static float volumnRatio1 = 0;


	public static void initSound(Context context) {

		sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

		spMap = new HashMap<Integer, Integer>();

		spMap.put(1, sp.load(context, R.raw.scan, 1));

		spMap.put(2, sp.load(context, R.raw.wrong, 1));

		spMap.put(3, sp.load(context, R.raw.new_order, 1));

		try {

			am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

			float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);

			volumnRatio1 = audioCurrentVolumn / audioMaxVolumn;

		} catch (Exception e) {

			Toast.makeText(context,  "ԵʼۯʹӴʱעʺխϳ:" + e.getMessage(), Toast.LENGTH_SHORT).show();


			StringWriter sw = new StringWriter();

			e.printStackTrace(new PrintWriter(sw, true));
		}

	}

}
