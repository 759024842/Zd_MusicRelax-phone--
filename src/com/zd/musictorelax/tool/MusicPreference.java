package com.zd.musictorelax.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class MusicPreference {

	SharedPreferences sharedPreferences;

	public MusicPreference(Context context) {
		sharedPreferences = context.getSharedPreferences("music_preference",Context.MODE_PRIVATE);
	}

	/**
	 * ��������˳�ʱ�Ĳ���״̬
	 * 
	 * @param context
	 * @param position
	 */
	public void savaPlayState(Context context, boolean isPlay) {
		sharedPreferences.edit().putBoolean("isPlay", isPlay).commit();
	}

	/**
	 * ��ȡ�˳�ʱ�Ĳ���״̬
	 * 
	 * @param context
	 * @return
	 */
	public boolean getPlayState(Context context) {
		return sharedPreferences.getBoolean("isPlay", false);
	}

	
	/**
	 * ���浱ǰ���Ÿ�������
	 * 
	 */
	public void savaMusicName(Context context, String strName) {
		sharedPreferences.edit().putString("musicName", strName).commit();
	}

	/**
	 * ��ȡ��ǰ���Ÿ�������
	 * 
	 */
	public String getMusicName(Context context) {
		return sharedPreferences.getString("musicName", "");
	}

	/**
	 * ���浱ǰ���Ÿ����б�λ��
	 * 
	 */
	public void savaMusicPosition(Context context, int position) {
		sharedPreferences.edit().putInt("position", position).commit();
	}

	/**
	 * ��ȡ��ǰ���Ÿ�������
	 * 
	 */
	public int getMusicPosition(Context context) {
		return sharedPreferences.getInt("position", 0);
	}

}
