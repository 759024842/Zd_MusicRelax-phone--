package com.zd.musictorelax.tool;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BroTools {
	public void broIntentStr(String strAction,String strKey,String strValue,Context context){
		Intent intent=new Intent();
		intent.setAction(strAction);
		intent.putExtra(strKey, strValue);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
	public void broIntentInt(String strAction,String strKey,int niValue,Context context){
		Intent intent=new Intent();
		intent.setAction(strAction);
		intent.putExtra(strKey, niValue);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
	public void broIntentBoolean(String strAction,String strKey,boolean isValue,Context context){
		Intent intent=new Intent();
		intent.setAction(strAction);
		intent.putExtra(strKey, isValue);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}
