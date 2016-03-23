package com.zd.musictorelax.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class MusicPreference {

	SharedPreferences sharedPreferences;

	public MusicPreference(Context context) {
		sharedPreferences = context.getSharedPreferences("music_preference",Context.MODE_PRIVATE);
	}

	/**
	 * 保存歌曲退出时的播放状态
	 * 
	 * @param context
	 * @param position
	 */
	public void savaPlayState(Context context, boolean isPlay) {
		sharedPreferences.edit().putBoolean("isPlay", isPlay).commit();
	}

	/**
	 * 获取退出时的播放状态
	 * 
	 * @param context
	 * @return
	 */
	public boolean getPlayState(Context context) {
		return sharedPreferences.getBoolean("isPlay", false);
	}

	
	/**
	 * 保存当前播放歌曲名称
	 * 
	 */
	public void savaMusicName(Context context, String strName) {
		sharedPreferences.edit().putString("musicName", strName).commit();
	}

	/**
	 * 获取当前播放歌曲名称
	 * 
	 */
	public String getMusicName(Context context) {
		return sharedPreferences.getString("musicName", "");
	}

	/**
	 * 保存当前播放歌曲列表位置
	 * 
	 */
	public void savaMusicPosition(Context context, int position) {
		sharedPreferences.edit().putInt("position", position).commit();
	}

	/**
	 * 获取当前播放歌曲名称
	 * 
	 */
	public int getMusicPosition(Context context) {
		return sharedPreferences.getInt("position", 0);
	}

}
