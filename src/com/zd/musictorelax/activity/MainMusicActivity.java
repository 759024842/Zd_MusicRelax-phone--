package com.zd.musictorelax.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.zd.musictorelax.fragment.LibFragment;
import com.zd.musictorelax.fragment.PlanFragment;
import com.zd.musictorelax.fragment.PlayFragment;
import com.zd.musictorelax.service.MusicBackService;
import com.zd.musictorelax.tool.BroTools;
import com.zd.musictorelax.tool.HomeWatcher;
import com.zd.musictorelax.tool.HomeWatcher.OnHomePressedListener;
import com.zd.musictorelax.tool.MusicPreference;
import com.zd.musictorelax.tool.NetWorkState;
import com.zd.musictorelax.tool.initDb;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TabWidget;
import android.widget.Toast;

public class MainMusicActivity extends FragmentActivity {
	private ViewPager mViewPager;

	private PagerAdapter mPagerAdapter;

	private TabWidget mTabWidget;

	private String[] addresses = { "方案", "播放", "乐库" };

	private View[] view = new View[addresses.length];

	private List<Fragment> mFragmentList;

	private static ArrayList<HashMap<String, Object>> dataPlay;

	// private AudioManager audioManager;// 音量管理者
	private NetWorkState netWorkState;
	private boolean isVisible;
	private BroTools broTools = new BroTools();
	public static MusicPreference musicPreference;
	private HomeWatcher mHomeWatcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(MainMusicActivity.this, MusicBackService.class);
		startService(intent);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main2);

		musicPreference = new MusicPreference(this);
		mFragmentList = new ArrayList<Fragment>();

		mTabWidget = (TabWidget) findViewById(R.id.tabWidget1);
		mTabWidget.setStripEnabled(false);
		view[0] = getLayoutInflater().inflate(R.layout.tab1, null);
		mTabWidget.addView(view[0]);
		view[0].setOnClickListener(mTabClickListener);

		view[1] = getLayoutInflater().inflate(R.layout.tab2, null);
		mTabWidget.addView(view[1]);
		view[1].setOnClickListener(mTabClickListener);

		view[2] = getLayoutInflater().inflate(R.layout.tab3, null);
		mTabWidget.addView(view[2]);
		view[2].setOnClickListener(mTabClickListener);

		mViewPager = (ViewPager) findViewById(R.id.viewPager1);
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList);
		mViewPager.setOffscreenPageLimit(3); // 用于限制可滑动的页面数，并且可以防止数据丢失
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);

		netWorkState = new NetWorkState();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(broReceiver, intentFilter);

		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction("android.intent.action.isvisible1");
		intentFilter1.addAction("android.intent.action.Lib");

		BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
			@SuppressWarnings("unchecked")
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("android.intent.action.isvisible1")) {
					isVisible = intent.getExtras().getBoolean("isVisible");
				} else if (intent.getAction().equals("android.intent.action.Lib")) {
					dataPlay = new ArrayList<HashMap<String, Object>>();
					dataPlay.clear();
					@SuppressWarnings("rawtypes")
					ArrayList data = intent.getExtras().getStringArrayList("datac");
					if (data != null) {
						for (int i = 0; i < data.size(); i++) {
							HashMap<String, Object> map1 = (HashMap<String, Object>) data.get(i);
							map1.put("name", map1.get("sName"));
							map1.put("splayTime", map1.get("splayTime"));
							map1.put("sFormat", map1.get("sFormat"));
							dataPlay.add(map1);
						}
					}
				}
			}
		};
		broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter1);
		intoPlay();
		// ImportDB();
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
				Log.i("sysout", "KeyEvent.KEYCODE_HOME");
				if (isVisible) {
					broTools.broIntentBoolean("android.intent.action.isvisible", "isVisible", true,
							getApplicationContext());
					isVisible = !isVisible;
				} else {
					MainMusicActivity.this.finish();
					unregisterReceiver(broReceiver);
				}
			}

			@Override
			public void onHomeLongPressed() {
				Log.e("sysout", "onHomeLongPressed");
			}
		});
		mHomeWatcher.startWatch();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// mHomeWatcher.stopWatch();// 在onPause中停止监听，不然会报错的。
	}

	private BroadcastReceiver broReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (netWorkState.isNetworkConnected(getApplicationContext())) {
				if (netWorkState.isMobileConnected(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), "是否使用手机网络", Toast.LENGTH_SHORT).show();
				} else if (netWorkState.isWifiConnected(getApplicationContext())) {
					return;
				}
			} else {
				Toast.makeText(getApplicationContext(), "请开启网络", Toast.LENGTH_SHORT).show();
			}

		}
	};

	@SuppressLint("NewApi")
	public void intoPlay() {
		mTabWidget.setCurrentTab(0);
		Fragment planFragment = new PlanFragment();
		mFragmentList.add(planFragment);
		Fragment playFragment = new PlayFragment();
		mFragmentList.add(playFragment);
		Fragment libFragment = new LibFragment();
		mFragmentList.add(libFragment);
	}

	public void ImportDB() {
		if (!new File(initDb.Path, initDb.dbName).exists()) {
			initDb init = new initDb(getApplicationContext());
			init.copyDb(initDb.dbName);// 加载本地数据库;
		}
	}

	private OnClickListener mTabClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == view[0]) {
				mViewPager.setCurrentItem(0);
			} else if (v == view[1]) {
				mViewPager.setCurrentItem(1);
			} else if (v == view[2]) {
				mViewPager.setCurrentItem(2);
			}
		}
	};

	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			mTabWidget.setCurrentTab(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	private class MyPagerAdapter extends FragmentStatePagerAdapter {
		private List<Fragment> list;

		public MyPagerAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}
	}

	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// Intent intent=new Intent();
	// intent.setClassName("com.zd.musictorelax.service","com.zd.musictorelax.service.MusicBackService");
	// stopService(intent);
	// }
	// private boolean isQiut;
	// private int niVoice;
	@SuppressLint("ShowToast")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		// int maxVolume =
		// audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		// int currentVolume =
		// audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		// Log.i("sysout", maxVolume+":"+currentVolume);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (isVisible) {
				broTools.broIntentBoolean("android.intent.action.isvisible", "isVisible", true,
						getApplicationContext());
				// Intent intent=new Intent();
				// intent.setAction("android.intent.action.isvisible");
				// intent.putExtra("isVisible", true);
				// LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
				isVisible = !isVisible;
			} else {
				MainMusicActivity.this.finish();
				unregisterReceiver(broReceiver);
				// moveTaskToBack(false);
				// return true;
			}
			break;
		case KeyEvent.KEYCODE_HOME:
			Log.i("sysout", "KeyEvent.KEYCODE_HOME");
			if (isVisible) {
				broTools.broIntentBoolean("android.intent.action.isvisible", "isVisible", true,
						getApplicationContext());
				isVisible = !isVisible;
			} else {
				MainMusicActivity.this.finish();
				unregisterReceiver(broReceiver);
			}
			break;
		}
		// case KeyEvent.KEYCODE_VOLUME_DOWN:
		// niVoice--;
		// if(currentVolume<=0){
		// niVoice=0;
		// }
		// break;
		// case KeyEvent.KEYCODE_VOLUME_UP:
		// niVoice++;
		// if(niVoice>=maxVolume){
		// niVoice=maxVolume;
		// }
		// break;
		// Intent intent = new Intent("android.intent.action.voice");
		// intent.putExtra("voice", niVoice);
		// LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		return false;
	}
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.activity_menu, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.menu_exit_false:
	// return true;
	// case R.id.menu_exit_true:
	// System.exit(0);
	// break;
	// }
	// return false;
	// }
}
