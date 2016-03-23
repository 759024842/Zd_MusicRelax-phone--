package com.zd.musictorelax.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.zd.musictorelax.activity.MainMusicActivity;
import com.zd.musictorelax.activity.R;
import com.zd.musictorelax.tool.MusicPreference;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MusicBackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		OnCompletionListener, OnBufferingUpdateListener {
	private static MediaPlayer player;
	private Handler mHandler = new Handler();// ��Ƶ����Ƶ�����߳�
	// ��ǰ���ŵ����ֺ�
	private int currentSongIndex = 0;
	// �Ƿ�ѭ������
	private boolean isRepeat = false;// �ظ�
	private boolean isPlay; // ����

	private ArrayList<Map<String, Object>> dataPLib = null;

	private final static int IS_PLAY = 1, NEXT_MUSIC = 2, PRE_MUSIC = 3, IS_LOOP = 4, PLAY_SONG = 5;
	private int current;
	private BroadcastReceiver mItemViewListClickReceiver;
	private Timer mTimer = new Timer(); // ��ʱ��

	private PlayerReceiver playR;

	public static MusicPreference musicPreference;
	private String strXmlPath = Environment.getExternalStorageDirectory() + "/xml/dataplay.xml";

	private Handler mhandler = new Handler();

	// �÷��񲻴�����Ҫ������ʱ�����ã�����startService()����bindService()��������ʱ���ø÷���
	@Override
	public void onCreate() {
		super.onCreate();

		Log.e("MusicBackservice", "���ֲ��ź�̨����");
		dataPLib = new ArrayList<Map<String, Object>>();

		musicPreference = new MusicPreference(MusicBackService.this);

		initBroadPlayR();

		initBroad();

		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		if (player != null) {
			player.setOnCompletionListener(this);
		}
	}

	private void initBroad() {
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.Play");
		intentFilter.addAction("android.intent.action.Song");
		intentFilter.addAction("android.intent.action.SongIndex");
		intentFilter.addAction("android.intent.action.CancelSong");
		intentFilter.addAction("android.intent.action.currentseek");

		mItemViewListClickReceiver = new BroadcastReceiver() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("android.intent.action.Play")) {
					current = intent.getExtras().getInt("control");
					Log.i("sysout", current + "");
					switch (current) {
					case IS_PLAY:
						musicPlayer();
						break;
					case NEXT_MUSIC:
						nextMusic();
						break;
					case PRE_MUSIC:
						upMusic();
						break;
					case IS_LOOP:
						repeatMusic();
						break;
					case PLAY_SONG:
						playSong(0);
						break;
					}
				}
				if (intent.getAction().equals("android.intent.action.Song")) {
					dataPLib.clear();
					ArrayList data = intent.getExtras().getStringArrayList("datalib");
					Log.i("sysout", data + "ser");
					if (data != null) {
						for (int i = 0; i < data.size(); i++) {
							HashMap<String, Object> map1 = (HashMap<String, Object>) data.get(i);
							map1.put("name", map1.get("name"));
							map1.put("time", map1.get("time"));
							map1.put("sFormat", map1.get("sFormat"));
							dataPLib.add(map1);
						}
						if (dataPLib != null && data.size() > 0) {
							// playSong(0);
							currentSongIndex = 0;
							isPlay = true;
							musicByRunnable();
						} else {
							player.stop();
							isPlay = false;
							setMsgNotification();
							Intent intent1 = new Intent();
							Intent intent2 = new Intent();
							Intent intent3 = new Intent();
							intent1.setAction("android.intent.action.curSong");
							intent1.putExtra("strName", "  ");
							intent2.setAction("action.play.state");
							intent2.putExtra("isPlay", false);
							intent3.setAction("android.intent.action.seek");
							intent3.putExtra("currentPos", 0);
							intent3.putExtra("duraPos", 1);
							LocalBroadcastManager.getInstance(MusicBackService.this).sendBroadcast(intent1);
							LocalBroadcastManager.getInstance(MusicBackService.this).sendBroadcast(intent2);
							LocalBroadcastManager.getInstance(MusicBackService.this).sendBroadcast(intent3);
						}
						Log.i("sysout", dataPLib + "map-service");
						Log.i("sysout", dataPLib.size() + "--size");
					}
				} else if (intent.getAction().equals("android.intent.action.CancelSong")) {
					dataPLib.clear();
					ArrayList list = intent.getExtras().getStringArrayList("datalib");
					Log.i("sysout", list + "dataC");
					for (int i = 0; i < list.size(); i++) {
						HashMap<String, Object> map1 = (HashMap<String, Object>) list.get(i);
						map1.put("name", map1.get("name"));
						map1.put("time", map1.get("time"));
						map1.put("sFormat", map1.get("sFormat"));
						dataPLib.add(map1);
						Log.i("sysout", dataPLib + "map");
					}
					if (dataPLib.size() <= 0) {
						player.stop();
						player.reset();
						isPlay = false;
						setMsgNotification();
						Intent intent1 = new Intent();
						Intent intent2 = new Intent();
						Intent intent3 = new Intent();
						intent1.setAction("android.intent.action.curSong");
						intent1.putExtra("strName", "  ");
						intent2.setAction("action.play.state");
						intent2.putExtra("isPlay", false);
						intent3.setAction("android.intent.action.seek");
						intent3.putExtra("currentPos", 0);
						intent3.putExtra("duraPos", 1);
						LocalBroadcastManager.getInstance(MusicBackService.this).sendBroadcast(intent1);
						LocalBroadcastManager.getInstance(MusicBackService.this).sendBroadcast(intent2);
						LocalBroadcastManager.getInstance(MusicBackService.this).sendBroadcast(intent3);
					}
				} else if (intent.getAction().equals("android.intent.action.SongIndex")) {
					int position = intent.getExtras().getInt("position");
					Log.i("sysout", position + "position");
					if (String.valueOf(position) != null) {
						currentSongIndex = position;
						// playSong(currentSongIndex);
						isPlay = true;
						musicByRunnable();
					}
				} else if (intent.getAction().equals("android.intent.action.currentseek")) {
					int seekPor = intent.getExtras().getInt("seekPor");
					if (seekPor > 0)
						player.seekTo(seekPor);
				}
			}
		};
		broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
	}

	private void initBroadPlayR() {
		playR = new PlayerReceiver();
		IntentFilter intentFilterP = new IntentFilter();
		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_LAST);
		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_CLOSE);
		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_REPEAT);
		registerReceiver(playR, intentFilterP);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	// ��������ͨ��bindService ����֪ͨ��Serviceʱ�÷���������
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i("sysout", "onbind--service");
		return null;
	}

	// ��������ͨ��unbindService����֪ͨ��Serviceʱ�÷���������
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("sysout", "onunbind--service");
		return super.onUnbind(intent);
	}

	/**
	 * �Ƿ񲥷�
	 */
	public void musicPlayer() {
		// ������
		if (dataPLib.size() > 0) {
			Intent intent = new Intent();
			intent.setAction("action.play.state");
			if (player.isPlaying()) {
				if (player != null) {
					player.pause();// ��ͣ����
					intent.putExtra("isPlay", false);
					isPlay = false;
					setMsgNotification();
				}
			} else {
				if (player != null) {
					player.start();// ��ʼ���� �� �ָ�����
					intent.putExtra("isPlay", true);
					isPlay = true;
					setMsgNotification();
				}
			}
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		} else {
			Toast.makeText(MusicBackService.this, "û�пɲ��Ÿ���", Toast.LENGTH_SHORT).show();
		}
		musicPreference.savaPlayState(MusicBackService.this, isPlay);
	}

	/**
	 * 
	 * ��һ��
	 * 
	 **/
	public void nextMusic() {
		// ��������һ��ʱ������һ�ײ���
		if (currentSongIndex < (dataPLib.size() - 1)) {
			currentSongIndex = currentSongIndex + 1;
			// playSong(currentSongIndex);
			musicByRunnable();
		}
		// �ص���һ��
		else {
			currentSongIndex = 0;
			musicByRunnable();
			// playSong(0);
		}
		musicPreference.savaMusicName(MusicBackService.this, (String) dataPLib.get(currentSongIndex).get("name"));
		musicPreference.savaMusicPosition(MusicBackService.this, currentSongIndex);
	}

	/**
	 * 
	 * ��һ��
	 * 
	 **/
	public void upMusic() {
		// ��������һ��ʱ������һ�ײ���
		if (currentSongIndex > 0) {
			currentSongIndex = currentSongIndex - 1;
			// playSong(currentSongIndex);
			musicByRunnable();
		}
		// �������һ��
		else {
			currentSongIndex = dataPLib.size() - 1;
			// playSong(currentSongIndex);
			musicByRunnable();
		}
		musicPreference.savaMusicName(MusicBackService.this, (String) dataPLib.get(currentSongIndex).get("name"));
		musicPreference.savaMusicPosition(MusicBackService.this, currentSongIndex);
	}

	/**
	 * 
	 * ����ظ�����
	 * 
	 **/
	public void repeatMusic() {
		Intent intent = new Intent();
		intent.setAction("action.play.repeat");
		if (isRepeat) {
			isRepeat = false;
			intent.putExtra("isRepeat", false);
			setMsgNotification();
		} else {
			isRepeat = true;
			intent.putExtra("isRepeat", true);
			setMsgNotification();
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	/*
	 * ���ص�ǰ���Ÿ�����ʱ��
	 */
	public static int getMusicDuration() {
		return player.getDuration();
	}

	/*
	 * ���ص�ǰ���Ÿ�������λ��
	 */
	public static int getMusicCurrPosition() {
		return player.getCurrentPosition();
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int position = player.getCurrentPosition();
			int duration = player.getDuration();
			if (duration > 0) {
				// ������ȣ���ȡ���������̶�*��ǰ���ֲ���λ�� / ��ǰ����ʱ����
				Intent intent = new Intent();
				intent.setAction("android.intent.action.seek");
				intent.putExtra("currentPos", position);
				intent.putExtra("duraPos", duration);
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}
		};
	};
	// ��ʱ��
	TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			if (player == null)
				return;
			if (player.isPlaying()) {
				handler.sendEmptyMessage(0); // ������Ϣ
			}
		}
	};
	// private Runnable mRunnable = new Runnable() {
	// @Override
	// public void run() {
	// playSong(currentSongIndex);
	// }
	// };

	private void musicByRunnable() {
		new Thread() {
			@Override
			public void run() {
				// mhandler.post(mRunnable);
				playSong(currentSongIndex);
			}
		}.start();
		Log.e("runLog", " musicByRunnable is called");

	}

	/**
	 * ����ָ������
	 **/
	public void playSong(int songIndex) {
		if (dataPLib != null && dataPLib.size() > 0) {
			// http://pu.zd-psy.com/music/������.��׺��
			String strName = (String) dataPLib.get(songIndex).get("name");
			String strFormat = (String) dataPLib.get(songIndex).get("sFormat");
			File file = new File(Environment.getExternalStorageDirectory() + "/ZhuoDunPlatForm/" + "/zd_music/",
					strName + "" + strFormat);// "/ZhuoDunPlatForm/"+
			Log.i("playSongInfo", strName + "" + strFormat);
			try {
				if (player.isPlaying()) {
					player.stop();
				}
				player.reset();// ���ò�����
				// Intent intentP=new Intent();
				// intentP.setAction("android.intent.action.loading");
				// LocalBroadcastManager.getInstance(this).sendBroadcast(intentP);
				if (file.exists()) {
					FileInputStream fis = new FileInputStream(file);
					// String
					// strPath=Environment.getExternalStorageDirectory()+"/zd_music/"+strName+""+strFormat;
					// player.setDataSource(strPath);//�����ּ����л�ȡҪ���ŵ�����·��
					player.setDataSource(fis.getFD());// �����ּ����л�ȡҪ���ŵ�����·��
				} else {

					// File file1 = new File(URI.create());
					// FileInputStream fis = new FileInputStream(file1);
					// URL url = new URL("http://pu.zd-psy.com/music/" + strName
					// + "" + strFormat);
					// URLConnection conn = url.openConnection();
					//
					// player.setDataSource(fis.getFD());
					String fileName = "http://pu.zd-psy.com/music/" + strName + "" + strFormat;
					Uri uri = Uri.parse(fileName);
					Log.e("fileName", fileName);
					player.setDataSource(fileName);// �����ּ����л�ȡҪ���ŵ�����·��
					// player.setOnBufferingUpdateListener(this);
				}
				// player.setDataSource("http://pu.zd-psy.com/music/"+strName+""+strFormat);//�����ּ����л�ȡҪ���ŵ�����·��
				player.prepare();// ׼������

				player.start(); // ����
				setMsgNotification();
				Intent intent = new Intent();
				Intent intent1 = new Intent();
				intent.setAction("android.intent.action.curSong");
				intent1.setAction("action.play.state");
				intent.putExtra("currentSong", songIndex);
				intent.putExtra("strName", strName);
				intent1.putExtra("isPlay", true);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
				if (player.isPlaying()) {
					mTimer.schedule(timerTask, 0, 1000);
				}
				// �������½�����
				updateProgressBar();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				// Toast.makeText(getApplicationContext(), "�����쳣",
				// Toast.LENGTH_SHORT).show();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * �������½�����
	 */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * ����������Ϻ�����֪ͨ
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (dataPLib.size() > 0) {
			// �ظ�����ģʽ
			if (isRepeat) {
				// playSong(currentSongIndex);
				musicByRunnable();
			}
			// ������һ��
			else {
				if (currentSongIndex < (dataPLib.size() - 1)) // ����Ƿ񲥷�����������
				{
					currentSongIndex = currentSongIndex + 1;
					// playSong(currentSongIndex);
					musicByRunnable();
				} else {
					currentSongIndex = 0;
					// playSong(0);
					musicByRunnable();
				}
			}
			musicPreference.savaMusicName(MusicBackService.this, (String) dataPLib.get(currentSongIndex).get("name"));
			musicPreference.savaMusicPosition(MusicBackService.this, currentSongIndex);
		}
	}

	/**
	 * ��������������
	 **/
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			try {
				if (player != null) {
					// long totalDuration = player.getDuration();// ��ȡ�ܲ��ų��ȣ����룩
					// long currentDuration = player.getCurrentPosition();//
					// ��ȡ��ǰ���ų��ȣ����룩
					// �߳�ѭ��
					mHandler.postDelayed(this, 100);
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (player != null) {
			player.release();
			player = null;
		}
		try {
			unregisterReceiver(mItemViewListClickReceiver);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("Receiver not registered")) {
			} else {
				throw e;
			}
		}
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
	}

	// @Override
	// public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
	// // TODO Auto-generated method stub
	// }
	// private SeekBar seekBar;
	/**
	 * �������
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.seek");
		intent.putExtra("percent", percent);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
		Log.i("sysout", percent + "buffer");
	}

	/**
	 * 
	 * �������ֵ���ʾ��
	 * 
	 */
	RemoteViews contentView;
	Notification mNotification;
	NotificationManager mNotificationManager;

	@SuppressWarnings("deprecation")
	private void setMsgNotification() {
		int icon = R.drawable.notify_img_logo; // ����֪ͨ����ͼ��
		CharSequence tickerText = "";// ֪ͨ��������ʾ
		mNotification = new Notification(icon, tickerText, 0); // ����֪ͨ��ʵ��

		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);

		mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		// Notification�а���һ��RemoteView�ؼ���ʵ�ʾ���֪ͨ��Ĭ����ʾ��View��ͨ������RemoteVIew�����Զ��岼��
		contentView = new RemoteViews(getPackageName(), R.layout.notify_view_item);
		contentView.setImageViewResource(R.id.img_notify_img, icon);
		if (isPlay) {
			contentView.setImageViewResource(R.id.btn_notify_play, R.drawable.notify_pause);
		} else {
			contentView.setImageViewResource(R.id.btn_notify_play, R.drawable.notify_play);
		}
		if (isRepeat) {
			contentView.setImageViewResource(R.id.btn_notify_repeat, R.drawable.one_notify_repeat);
		} else {
			contentView.setImageViewResource(R.id.btn_notify_repeat, R.drawable.list_notify_repeat);
		}

		if (dataPLib != null && dataPLib.size() > 0) {
			contentView.setTextViewText(R.id.txt_notify_music, dataPLib.get(currentSongIndex).get("name") + "");
		}
		Intent buttonPlayIntent = new Intent(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_LAST); // ----����֪ͨ����ť�㲥
		PendingIntent pendButtonPlayIntent = PendingIntent.getBroadcast(this, 0, buttonPlayIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.btn_notify_back, pendButtonPlayIntent);

		Intent buttonPlayIntent1 = new Intent(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_PLAY); // ----����֪ͨ����ť�㲥
		PendingIntent pendButtonPlayIntent1 = PendingIntent.getBroadcast(this, 0, buttonPlayIntent1,
				PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.btn_notify_play, pendButtonPlayIntent1);

		Intent buttonPlayIntent2 = new Intent(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_NEXT); // ----����֪ͨ����ť�㲥
		PendingIntent pendButtonPlayIntent2 = PendingIntent.getBroadcast(this, 0, buttonPlayIntent2,
				PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.btn_notify_next, pendButtonPlayIntent2);
		Intent buttonPlayIntent3 = new Intent(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_CLOSE); // ----����֪ͨ����ť�㲥
		PendingIntent pendButtonPlayIntent3 = PendingIntent.getBroadcast(this, 0, buttonPlayIntent3,
				PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.btn_notify_cancel, pendButtonPlayIntent3);
		Intent buttonPlayIntent4 = new Intent(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_REPEAT); // ----����֪ͨ����ť�㲥
		PendingIntent pendButtonPlayIntent4 = PendingIntent.getBroadcast(this, 0, buttonPlayIntent4,
				PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.btn_notify_repeat, pendButtonPlayIntent4);
		// ���ð�ť����¼�������Ҫ��һ��PendingIntent
		// ע��ֻ������sdk3.0���ϵ�ϵͳ�У�֪ͨ���еİ�ť����¼�������Ӧ��������üӸ�������sdk3.0���£�����ʾ��ť
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			contentView.setViewVisibility(R.id.btn_notify_back, View.VISIBLE);
			contentView.setViewVisibility(R.id.btn_notify_play, View.VISIBLE);
			contentView.setViewVisibility(R.id.btn_notify_next, View.VISIBLE);
			contentView.setViewVisibility(R.id.btn_notify_cancel, View.VISIBLE);
			contentView.setViewVisibility(R.id.btn_notify_repeat, View.VISIBLE);
		}
		Intent notificationIntent = new Intent(getApplicationContext(), MainMusicActivity.class);
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mNotification.setLatestEventInfo(getApplicationContext(), "", "", contentIntent);
		mNotification.contentView = contentView;
		mNotificationManager.notify(1, mNotification);
	}

	/**
	 * ���ֲ������
	 * 
	 * @author xu
	 *
	 */
	public class PlayerReceiver extends BroadcastReceiver {
		public static final String NOTIFICATION_ITEM_BUTTON_LAST = "pre";// ----֪ͨ����һ�װ�ť
		public static final String NOTIFICATION_ITEM_BUTTON_PLAY = "pause";// ----֪ͨ�����Ű�ť
		public static final String NOTIFICATION_ITEM_BUTTON_NEXT = "next";// ----֪ͨ����һ�װ�ť
		public static final String NOTIFICATION_ITEM_BUTTON_CLOSE = "close";// ----֪ͨ���رհ�ť
		public static final String NOTIFICATION_ITEM_BUTTON_REPEAT = "repeat";// ----֪ͨ���ظ���ť

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(NOTIFICATION_ITEM_BUTTON_CLOSE)) {
				File file = new File(strXmlPath);
				if (file.isFile()) {
					file.delete();
				}
				if (mNotificationManager != null) {
					mNotificationManager.cancelAll();
				}
				System.exit(0);
			} else if (action.equals(NOTIFICATION_ITEM_BUTTON_PLAY)) {
				musicPlayer();
			} else if (action.equals(NOTIFICATION_ITEM_BUTTON_NEXT)) {
				nextMusic();
			} else if (action.equals(NOTIFICATION_ITEM_BUTTON_LAST)) {
				upMusic();
			} else if (action.equals(NOTIFICATION_ITEM_BUTTON_REPEAT)) {
				repeatMusic();
			}
		}
	}
}
