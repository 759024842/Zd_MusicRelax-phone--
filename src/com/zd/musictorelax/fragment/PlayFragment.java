package com.zd.musictorelax.fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.zd.musictorelax.activity.R;
import com.zd.musictorelax.adapter.PlayListAdapter;
import com.zd.musictorelax.adapter.PlayListAdapter.ViewHolderP;
import com.zd.musictorelax.handler.SongPFeed;
import com.zd.musictorelax.handler.SongPHandler;
import com.zd.musictorelax.service.MusicBackService;
import com.zd.musictorelax.tool.MusicPreference;
import com.zd.musictorelax.tool.SaxWriteXml;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

public class PlayFragment extends Fragment implements OnClickListener,OnCheckedChangeListener,SeekBar.OnSeekBarChangeListener {
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;
	
	private Button btnBack; // ��һ��
	private Button btnNext; // ��һ��
	private Button btnPlay; // ����
	private Button btnCancel;//ɾ��
	private Button btnLoop; // ѭ��
//	private Button btnVoice;// ����

	private CheckBox cbCancel; // ȫѡ��ť
	private View viewLine;
	//private TextView txtCurrentTime;// ��ǰ����ʱ��
    private TextView txtLoop,txtCurrentName;// ��ǰ������
    private SeekBar songProgressBar;// ��ǰ����
// 	private Utilities utilit; // ����ʱ��ת��Ϊ��עʱ���ʽ
 	
	private Context context;
	private static ArrayList<HashMap<String, Object>>  dataPlay;//�����б�����
	private ListView listView; //
	private PlayListAdapter playAdapter; //�����б�������
//	private PlayerReceiver playR; 
	private String strXmlPath=Environment.getExternalStorageDirectory()+"/xml/dataplay.xml";
	//��ع㲥
	private BroadcastReceiver mItemViewListClickReceiver;
	private BroadcastReceiver mItemViewListClickReceiver1;
	private BroadcastReceiver mItemViewListClickReceiver2;
	private BroadcastReceiver mItemViewListClickReceiver3;
	
	private boolean isRepeat;
    private static boolean isPlay;
	
	public SongPFeed songPFeed;
	private int currentSongIndex; //��ǰ���Ÿ���
	private int currentPor;//��ǰ���ֲ���ʱ��
	private int duraPos;   //���ֲ�����ʱ��
	public  MusicPreference musicPreference;
	private SaxWriteXml saxWriteXml = new SaxWriteXml();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		View view = inflater.inflate(R.layout.play_fragment,container,false);
		
		musicPreference = new MusicPreference(context);
		//ע����ʾ��㲥		
//		playR = new PlayerReceiver();
//		IntentFilter intentFilterP = new IntentFilter();
//		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_LAST);
//		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
//		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
//		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_CLOSE);
//		intentFilterP.addAction(PlayerReceiver.NOTIFICATION_ITEM_BUTTON_REPEAT);	
//		((Activity) context).registerReceiver(playR, intentFilterP);
		/** ��ʼ���ؼ� **/
		btnPlay = (Button) view.findViewById(R.id.btn_main_play);
		btnBack = (Button) view.findViewById(R.id.btn_main_back);
		btnNext = (Button) view.findViewById(R.id.btn_main_next);
		btnCancel = (Button) view.findViewById(R.id.btn_play_cancel);
		btnLoop = (Button) view.findViewById(R.id.btn_main_loop);
//		btnBluetooth = (Button) view.findViewById(R.id.btn_main_bluetooth);
//		btnVoice = (Button) view.findViewById(R.id.btn_main_voice);

		cbCancel = (CheckBox) view.findViewById(R.id.cbx_play_list);
		cbCancel.setOnCheckedChangeListener(this);
        
		viewLine=(View)view.findViewById(R.id.view_line1);
		
		btnPlay.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnLoop.setOnClickListener(this);
//		btnBluetooth.setOnClickListener(this);
//		btnVoice.setOnClickListener(this);

		txtCurrentName = (TextView) view.findViewById(R.id.txt_music_name);
		txtLoop=(TextView)view.findViewById(R.id.txt_loop);
		
		listView = (ListView) view.findViewById(R.id.list_play);
		listView.setOnItemClickListener(listenerPlay);
		listView.setOnItemLongClickListener(listenerLPlay);
		dataPlay = new ArrayList<HashMap<String, Object>>();
		
		/** ���ֲ�����ر��� **/
//		utilit = new Utilities();
		//��ʼ��������
    	songProgressBar = (SeekBar) view.findViewById(R.id.music_progress);
	    songProgressBar.setOnSeekBarChangeListener(this);
		//��ʼ����������
//		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);// ��õ�ǰ����
	    Log.i("sysout",dataPlay+"dataNew");
	    //�����˳�������ʱ�����б��xml�ļ�
	    File file=new File(strXmlPath);
	    if(file.exists()){
		songPFeed=getSongPFeed();
     	showSong();
     	if(dataPlay!=null && dataPlay.size()>0){
     	txtCurrentName.setText(musicPreference.getMusicName(context));
     	playAdapter.cur=musicPreference.getMusicPosition(context);
     	if(musicPreference.getPlayState(context))
     		btnPlay.setBackgroundResource(R.drawable.btn_pause);
     	else{
			btnPlay.setBackgroundResource(R.drawable.btn_play);
     	}
     	//Log.i("sysout", musicPreference.getMusicName(context)+":"+musicPreference.getMusicPosition(context)+":"+musicPreference.getPlayState(context));
     	}
	    }
	    
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.Lib");
		intentFilter.addAction("android.intent.action.voice");
		intentFilter.addAction("android.intent.action.seek");
		intentFilter.addAction("android.intent.action.isvisible");

	    mItemViewListClickReceiver = new BroadcastReceiver() {
			@SuppressWarnings("unchecked")
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("android.intent.action.Lib")) {
					dataPlay.clear();
					@SuppressWarnings("rawtypes")
					ArrayList data = intent.getExtras().getStringArrayList("datac");
					Log.i("sysout", data + "11");
					if (data != null) {
						for (int i = 0; i < data.size(); i++) {
							HashMap<String, Object> map1 = (HashMap<String, Object>) data.get(i);
							map1.put("name", map1.get("sName"));
							map1.put("splayTime", map1.get("splayTime"));
							map1.put("sFormat", map1.get("sFormat"));
							dataPlay.add(map1);
						}
						Log.i("sysout", dataPlay + "map"+dataPlay.size());
						getMusicLib();
						intent.setAction("android.intent.action.Song");
						intent.putExtra("datalib",dataPlay);
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
					}
				}
				else if(intent.getAction().equals("android.intent.action.seek")){
					currentPor=intent.getExtras().getInt("currentPos");
					duraPos=intent.getExtras().getInt("duraPos");
					//int percent=intent.getExtras().getInt("percent");
					int seekPor=songProgressBar.getMax()*currentPor/duraPos;
					songProgressBar.setProgress(seekPor);
					//songProgressBar.setSecondaryProgress(percent);
					}
				else if(intent.getAction().equals("android.intent.action.isvisible")){
					boolean isVisible=intent.getExtras().getBoolean("isVisible");
					if(isVisible){
						cbCancel.setChecked(false);
						btnCancel.setVisibility(View.GONE);
						cbCancel.setVisibility(View.GONE);
						viewLine.setVisibility(View.GONE);
						isPlayE=false;
						for (int i = 0; i < dataPlay.size(); i++) {
							playAdapter.getIsSelected().put(i, false);
							playAdapter.getIsCbVisible().put(i, View.INVISIBLE);
						}
						playAdapter.notifyDataSetChanged();
					}
					}
//				else if (intent.getAction().equals("android.intent.action.voice")) {
//					int niVoice=intent.getExtras().getInt("voice");
//				        Log.i("sysout","voice:"+niVoice);
//						if(String.valueOf(niVoice)!=null){
//						if(niVoice==0)
//							btnVoice.setBackgroundResource(R.drawable.voice_false);
//						else
//							btnVoice.setBackgroundResource(R.drawable.voice_true);
//						}
//				}
			}
		};
		broadcastManager.registerReceiver(mItemViewListClickReceiver,intentFilter);		
		
		LocalBroadcastManager broadcastManager1 = LocalBroadcastManager.getInstance(context);
		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction("action.play.state");
	    mItemViewListClickReceiver1= new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("action.play.state")) {
					isPlay=intent.getExtras().getBoolean("isPlay");
					Log.i("sysout", isPlay+"");
					if(isPlay){
						btnPlay.setBackgroundResource(R.drawable.btn_pause);
					    //setMsgNotification();
					}
					else{
						btnPlay.setBackgroundResource(R.drawable.btn_play);
				        //setMsgNotification();
					}
				}
			}
		};
		broadcastManager1.registerReceiver(mItemViewListClickReceiver1,intentFilter1);
		
		LocalBroadcastManager broadcastManager2 = LocalBroadcastManager.getInstance(context);
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2.addAction("action.play.repeat");
	    mItemViewListClickReceiver2= new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals("action.play.repeat")){
				   isRepeat=intent.getExtras().getBoolean("isRepeat");
					Log.i("sysout", isRepeat+"repeat");
					if(isRepeat){
						btnLoop.setBackgroundResource(R.drawable.one_music);
						Log.i("sysout",isRepeat+"");
						txtLoop.setText("����ѭ��");
						//setMsgNotification();
					}
					else{
						txtLoop.setText("˳�򲥷�");
						btnLoop.setBackgroundResource(R.drawable.img_btn_repeat_pressed);
						//setMsgNotification();
					}
				}
			}
		};
		broadcastManager2.registerReceiver(mItemViewListClickReceiver2,intentFilter2);	
		
		LocalBroadcastManager broadcastManager3 = LocalBroadcastManager.getInstance(context);
		IntentFilter intentFilter3 = new IntentFilter();
		intentFilter3.addAction("android.intent.action.curSong");
	    mItemViewListClickReceiver3= new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("android.intent.action.curSong")){
					 currentSongIndex=intent.getExtras().getInt("currentSong");
					 String musicName=intent.getStringExtra("strName");
					 Log.i("sysout", currentSongIndex+"current"+musicName);
					 playAdapter.cur=currentSongIndex;
					 Log.i("sysout", playAdapter.cur+"playAdapter.cur");
					 //�Զ��ƶ�
					 listView.setSelection(currentSongIndex-1);
					 playAdapter.notifyDataSetChanged();
					 txtCurrentName.setText(musicName);
				}
			}
		};
     	broadcastManager3.registerReceiver(mItemViewListClickReceiver3,intentFilter3);	

		return view;
	}


	/**
	 * ��ʾ�����б�
	 */
	public void getMusicLib() {
		Log.i("sysout",dataPlay+"shuju");
		playAdapter = new PlayListAdapter(dataPlay,context);//PlanFragment.dataLib
		playAdapter.notifyDataSetChanged();
		listView.setAdapter(playAdapter);
	}
//	private AudioManager audioManager;// ����������
//	private int currentVolume;// ��ǰ����
//	private boolean isVoice; // ��ǰ�Ƿ��������
	/**
	 * 
	 * ��ʼ������
	 * 
	 */
//	public void voiceOfMuisc() {
//		if (isVoice) {
//			isVoice = false;
//			currentVolume = 0;
//			btnVoice.setBackgroundResource(R.drawable.voice_false);
//			audioManager.setStreamVolume(AudioManager.STREAM_RING,
//					currentVolume, AudioManager.FLAG_ALLOW_RINGER_MODES);
//		} else {
//			isVoice = true;
//			btnVoice.setBackgroundResource(R.drawable.voice_true);
//			audioManager.setStreamVolume(AudioManager.STREAM_RING, 2,
//					AudioManager.FLAG_ALLOW_RINGER_MODES);
//		}
//	}
	private StringBuffer sb;
	/** ��ȡSD��xml�ļ� **/
	public void readXmlFromSd(String strName) {
		File f = new File(strName);
		String path = f.getAbsolutePath();
		FileInputStream fileIS = null;
		if(f.exists()){
		try {
			fileIS = new FileInputStream(path);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		sb = new StringBuffer();
		
		BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
		String readString = new String();
		try {
			while ((readString = buf.readLine()) != null) {
				sb.append(readString);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		}
	}
	/**��������xml�ļ�**/
	public SongPFeed getSongPFeed(){
		readXmlFromSd(strXmlPath);// STR_XML_NAME
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			SongPHandler handler = new SongPHandler();
			reader.setContentHandler(handler);
			if(sb!=null){
			String s1 = sb.toString();
			reader.parse(new InputSource(new StringReader(s1)));
			}
			return handler.getFeed();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}
	private void showSong() {
		if (songPFeed == null) {
			return;
		}
		//dataPlay = new ArrayList<HashMap<String, Object>>();
		dataPlay = songPFeed.getSaveForListView(context);
		Log.i("sysout", dataPlay+"newData");
		playAdapter=new PlayListAdapter(dataPlay,context);
		listView.setAdapter(playAdapter);
	}
	//���ֲ���״̬
	public void musicState(int niId){
		Intent intent = new Intent();
		intent.setClass(context, MusicBackService.class);
		intent.putExtra("control", niId);
		intent.setAction("android.intent.action.Play");
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		context.startService(intent);
	}
	
	//ɾ��ѡ�����ֺ��״̬
	private void cancelAfterMusic(String strAction){
		cbCancel.setChecked(false);
		btnCancel.setVisibility(View.GONE);
		cbCancel.setVisibility(View.GONE);
		viewLine.setVisibility(View.GONE);
		isPlayE=false;
		for (int i = 0; i < dataPlay.size(); i++) {
			playAdapter.getIsSelected().put(i, false);
			playAdapter.getIsCbVisible().put(i, View.INVISIBLE);
		}
		playAdapter.notifyDataSetChanged();
		Intent intent=new Intent();
		intent.setAction(strAction);
		intent.putExtra("datalib", dataPlay);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		context.startService(intent);
	}
	@Override
	public void onClick(View v) {	
		switch (v.getId()) {
		case R.id.btn_main_play:
			musicState(1);
			break;
		case R.id.btn_main_next:
			musicState(2);
			break;
		case R.id.btn_main_back:
			musicState(3);
			break;
		case R.id.btn_main_loop:
			musicState(4);
			break;
		case R.id.btn_main_voice:
			//voiceOfMuisc();
			break;
		case R.id.btn_play_cancel:
			if (cbCancel.isChecked()){
				if (dataPlay != null) {
					dataPlay.removeAll(dataPlay);
					cancelAfterMusic("android.intent.action.Song");
				}else
					Toast.makeText(context, "û�п�ɾ������", Toast.LENGTH_SHORT).show();
			    }else {
				if (dataPlayC != null && dataPlay != null) {
					cancelMusic();
					cancelAfterMusic("android.intent.action.CancelSong");
				}else
					Toast.makeText(context, "û�п�ɾ������", Toast.LENGTH_SHORT).show();
			}
			break;
//		case R.id.btn_main_bluetooth:
//			if (mConnectedDeviceName != null) {
//				currentDevice(mConnectedDeviceName);
//			} else {
//				currentDevice("��");
//			}
//			break;
		}
	}

	private ArrayList<HashMap<String, Object>> dataPlayC;
	private int count2;
	private int playPosition = -1;
	private boolean isCancel;
	private boolean isPlayE;
	// �ɲ��ż���
	private OnItemClickListener listenerPlay = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view1, int position,long arg3) {
			if(isPlayE){
			isCancel = true;
			ViewHolderP holder = (ViewHolderP) view1.getTag();
			holder.cb.toggle();
			dataPlayC = new ArrayList<HashMap<String, Object>>();
			if (holder.cb.isChecked()) {
				playAdapter.getIsSelected().put(position,holder.cb.isChecked());
				for (int i = 0; i < dataPlay.size(); i++) {
					HashMap<String, Object> mapPlay = new HashMap<String, Object>();
					if (playAdapter.getIsSelected().get(i)) {
						mapPlay.put("name", dataPlay.get(i).get("name"));
						mapPlay.put("splayTime", dataPlay.get(i).get("splayTime"));
					}
					dataPlayC.add(mapPlay);
				}
				count2++;
				if (count2 != dataPlay.size()) {
					cbCancel.setChecked(false);
				}
			} else {
				playAdapter.getIsSelected().put(position, false);
				for (int i = 0; i < dataPlay.size(); i++) {
					HashMap<String, Object> mapPlay = new HashMap<String, Object>();
					if (playAdapter.getIsSelected().get(i) == true) {
						mapPlay.put("name", dataPlay.get(i).get("name"));
						mapPlay.put("splayTime", dataPlay.get(i).get("splayTime"));
					}
					dataPlayC.add(mapPlay);
				}
				count2--;
				if (count2 != dataPlay.size()) {
					cbCancel.setChecked(false);
					playAdapter.getIsSelected().put(position, false);
					playPosition = position;
				}
			}
			playAdapter.notifyDataSetChanged();
			}
			else{
				playAdapter.cur=position;
				playAdapter.notifyDataSetChanged();
				Intent intent = new Intent("android.intent.action.SongIndex");
				intent.putExtra("position", position);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			}
		}
	};
	/**���������б������--��ʾɾ���ؼ�**/
	private OnItemLongClickListener listenerLPlay = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
            btnCancel.setVisibility(View.VISIBLE);
            cbCancel.setVisibility(View.VISIBLE);
            viewLine.setVisibility(View.VISIBLE);
            for(int i=0;i<dataPlay.size();i++){
            	playAdapter.getIsCbVisible().put(i, View.VISIBLE);
            }
            isPlayE=true;
			playAdapter.notifyDataSetChanged();
			Intent intent=new Intent();
		    intent.setAction("android.intent.action.isvisible1");
			intent.putExtra("isVisible", true);
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			return true;
		}
	};
	/** ɾ�����׸��� **/
	public void cancelMusic() {
		for (int i = 0; i < dataPlay.size(); i++) {
			for (int j = 0; j < dataPlayC.size(); j++) {
				if (dataPlayC.get(j).get("name") != null
						&& dataPlay.get(i).get("name") == dataPlayC.get(j).get(
								"name")) {
					if (i == playAdapter.cur) {
						dataPlay.remove(i);
						if (playAdapter.cur < dataPlay.size()) {
							Intent intent = new Intent(
									"android.intent.action.SongIndex");
							intent.putExtra("position", playAdapter.cur);
							LocalBroadcastManager.getInstance(context)
									.sendBroadcast(intent);
						} else {
							Intent intent = new Intent(
									"android.intent.action.SongIndex");
							intent.putExtra("position", 0);
							LocalBroadcastManager.getInstance(context)
									.sendBroadcast(intent);
						}
					} else {
						if (i < playAdapter.cur) {
							playAdapter.cur--;
						}
						dataPlay.remove(i);
					}
				}
			}
		}
		playAdapter.notifyDataSetChanged();
		listView.setAdapter(playAdapter);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean arg1) {
		switch (buttonView.getId()) {
		case R.id.cbx_play_list:
			for (int i = 0; i < dataPlay.size(); i++) {
				if (cbCancel.isChecked()) {
					playAdapter.getIsSelected().put(i, true);					
					count2 = dataPlay.size();
				} else {
					if (isCancel) {
						if (i == playPosition) {
							playAdapter.getIsSelected().put(i, false);
						} else {
							playAdapter.getIsSelected().put(i, true);
						}
						playPosition = -1;
					} else {
						playAdapter.getIsSelected().put(i, false);
						count2 = 0;
					}
				}
			}
			isCancel = false;
			playAdapter.notifyDataSetChanged();
			break;
		}
	}
	


	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
	}


	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
		
	}

	@Override
	public void onDestroy() {
		//Log.i("sysout", dataPlay+"xml"+":"+isPlay);
		//SaxWriteXml saxWriteXml = new SaxWriteXml();
		
		if(dataPlay!=null && dataPlay.size()>0){
		saxWriteXml.writeToXml(saxWriteXml.savexml(dataPlay),"dataplay.xml");
		musicPreference.savaMusicName(context, (String)dataPlay.get(currentSongIndex).get("sName"));
		musicPreference.savaMusicPosition(context, currentSongIndex);
	    musicPreference.savaPlayState(context, isPlay);
		}
		Log.e(TAG, "--- ON DESTROY ---");
		
//		if(playR!=null)
//			((Activity)context).unregisterReceiver(playR);
		try{
		if(mItemViewListClickReceiver!=null){
			 LocalBroadcastManager.getInstance(context).unregisterReceiver(mItemViewListClickReceiver);
		}
		if(mItemViewListClickReceiver1!=null){
			 LocalBroadcastManager.getInstance(context).unregisterReceiver(mItemViewListClickReceiver1);
		}
		if(mItemViewListClickReceiver2!=null){
			 LocalBroadcastManager.getInstance(context).unregisterReceiver(mItemViewListClickReceiver2);
		}
		if(mItemViewListClickReceiver3!=null){
			LocalBroadcastManager.getInstance(context).unregisterReceiver(mItemViewListClickReceiver3);
		}
		}catch (IllegalArgumentException e) {e.printStackTrace();}
		
//		if(dataPlay!=null){
//			dataPlay.clear();
//		}
//		if(mNotificationManager!=null){
//			mNotificationManager.cancelAll();
//		}
//		System.exit(0);
		super.onDestroy();
	}


	/**
	 * 
	 * �������ֵ���ʾ��
	 * 
	 */
//	RemoteViews contentView;
//	Notification mNotification;
//	NotificationManager mNotificationManager;
//    private boolean isRepeat;
//    private boolean isPlay;
//	@SuppressWarnings("deprecation")
//	private void setMsgNotification() {
//		int icon = R.drawable.notify_img_logo; // ����֪ͨ����ͼ��
//		CharSequence tickerText = "";// ֪ͨ��������ʾ
//		mNotification = new Notification(icon, tickerText, 0); // ����֪ͨ��ʵ��
//
//		String ns = Context.NOTIFICATION_SERVICE;
//		mNotificationManager = (NotificationManager) ((Activity) context).getSystemService(ns);
//
//		mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
//		// Notification�а���һ��RemoteView�ؼ���ʵ�ʾ���֪ͨ��Ĭ����ʾ��View��ͨ������RemoteVIew�����Զ��岼��
//		contentView = new RemoteViews(context.getPackageName(),R.layout.notify_view_item);
//		contentView.setImageViewResource(R.id.img_notify_img, icon);
//		if (isPlay) {
//			contentView.setImageViewResource(R.id.btn_notify_play,R.drawable.notify_pause);
//		} else {
//			contentView.setImageViewResource(R.id.btn_notify_play,R.drawable.notify_play);
//		}
//		
//		if (isRepeat) {
//			contentView.setImageViewResource(R.id.btn_notify_repeat,R.drawable.one_notify_repeat);
//		} else {
//			contentView.setImageViewResource(R.id.btn_notify_repeat,R.drawable.list_notify_repeat);
//		}
//		
//		if(dataPlay!=null && dataPlay.size()>0){
//		contentView.setTextViewText(R.id.txt_notify_music,dataPlay.get(currentSongIndex).get("name") + ""); 
//		}
//		Intent buttonPlayIntent = new Intent(
//				PlayerReceiver.NOTIFICATION_ITEM_BUTTON_LAST);//----����֪ͨ����ť�㲥
//		PendingIntent pendButtonPlayIntent = PendingIntent
//				.getBroadcast(context, 0, buttonPlayIntent,
//						PendingIntent.FLAG_UPDATE_CURRENT);
//		contentView.setOnClickPendingIntent(R.id.btn_notify_back,
//				pendButtonPlayIntent);
//
//		Intent buttonPlayIntent1 = new Intent(
//				PlayerReceiver.NOTIFICATION_ITEM_BUTTON_PLAY); // ----����֪ͨ����ť�㲥
//		PendingIntent pendButtonPlayIntent1 = PendingIntent.getBroadcast(
//				context, 0, buttonPlayIntent1,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//		contentView.setOnClickPendingIntent(R.id.btn_notify_play,
//				pendButtonPlayIntent1);
//
//		Intent buttonPlayIntent2 = new Intent(
//				PlayerReceiver.NOTIFICATION_ITEM_BUTTON_NEXT); // ----����֪ͨ����ť�㲥
//		PendingIntent pendButtonPlayIntent2 = PendingIntent.getBroadcast(
//				context, 0, buttonPlayIntent2,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//		contentView.setOnClickPendingIntent(R.id.btn_notify_next,
//				pendButtonPlayIntent2);
//		Intent buttonPlayIntent3 = new Intent(
//				PlayerReceiver.NOTIFICATION_ITEM_BUTTON_CLOSE); // ----����֪ͨ����ť�㲥
//		PendingIntent pendButtonPlayIntent3 = PendingIntent.getBroadcast(
//				context, 0, buttonPlayIntent3,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//		contentView.setOnClickPendingIntent(R.id.btn_notify_cancel,
//				pendButtonPlayIntent3);		
//		Intent buttonPlayIntent4 = new Intent(
//				PlayerReceiver.NOTIFICATION_ITEM_BUTTON_REPEAT); // ----����֪ͨ����ť�㲥
//		PendingIntent pendButtonPlayIntent4 = PendingIntent.getBroadcast(
//				context, 0, buttonPlayIntent4,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//		contentView.setOnClickPendingIntent(R.id.btn_notify_repeat,
//				pendButtonPlayIntent4);
//		// ���ð�ť����¼�������Ҫ��һ��PendingIntent
//		// ע��ֻ������sdk3.0���ϵ�ϵͳ�У�֪ͨ���еİ�ť����¼�������Ӧ��������üӸ�������sdk3.0���£�����ʾ��ť
//		if (android.os.Build.VERSION.SDK_INT >= 11) {
//			contentView.setViewVisibility(R.id.btn_notify_back, View.VISIBLE);
//			contentView.setViewVisibility(R.id.btn_notify_play, View.VISIBLE);
//			contentView.setViewVisibility(R.id.btn_notify_next, View.VISIBLE);
//			contentView.setViewVisibility(R.id.btn_notify_cancel, View.VISIBLE);
//			contentView.setViewVisibility(R.id.btn_notify_repeat, View.VISIBLE);
//		}
//		Intent notificationIntent = new Intent(context, context.getClass());
//		notificationIntent.setAction(Intent.ACTION_MAIN);
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        
//		mNotification.setLatestEventInfo(context, "", "",contentIntent);
//		mNotification.contentView = contentView;
//		mNotificationManager.notify(1, mNotification);
//	}
	
//	RemoteViews contentView;
//	Notification mNotification;
//	NotificationManager mNotificationManager;
//	@SuppressWarnings("deprecation")
//	private void setMsgNotification() {
//		int icon = R.drawable.notify_img_logo; // ����֪ͨ����ͼ��
//		CharSequence tickerText = "";// ֪ͨ��������ʾ
//		mNotification = new Notification(icon, tickerText, 0); // ����֪ͨ��ʵ��
//
//		String ns = Context.NOTIFICATION_SERVICE;
//		mNotificationManager = (NotificationManager) ((Activity) context).getSystemService(ns);
//
//		mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
//		// Notification�а���һ��RemoteView�ؼ���ʵ�ʾ���֪ͨ��Ĭ����ʾ��View��ͨ������RemoteVIew�����Զ��岼��
//		
//		Intent notificationIntent = new Intent(context, context.getClass());
//		notificationIntent.setAction(Intent.ACTION_MAIN);
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        
//		mNotification.setLatestEventInfo(context, "", "",contentIntent);
//		mNotification.contentView = contentView;
//		mNotificationManager.notify(1, mNotification);
//	}
		
//	public class PlayerReceiver extends BroadcastReceiver {
//		public static final String NOTIFICATION_ITEM_BUTTON_LAST = "pre";// ----֪ͨ����һ�װ�ť
//		public static final String NOTIFICATION_ITEM_BUTTON_PLAY = "pause";// ----֪ͨ�����Ű�ť
//		public static final String NOTIFICATION_ITEM_BUTTON_NEXT = "next";// ----֪ͨ����һ�װ�ť
//		public static final String NOTIFICATION_ITEM_BUTTON_CLOSE = "close";// ----֪ͨ����һ�װ�ť
//		public static final String NOTIFICATION_ITEM_BUTTON_REPEAT = "repeat";// ----֪ͨ����һ�װ�ť
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(NOTIFICATION_ITEM_BUTTON_CLOSE)) {
//				File file=new File(strXmlPath);
//				if(file.isFile()){
//					file.delete();
//				}
//				if(mNotificationManager!=null){
//				mNotificationManager.cancelAll();
//				}
//				((Activity)context).finish();
//				System.exit(0);
//			} else if (action.equals(NOTIFICATION_ITEM_BUTTON_PLAY)) {
//				musicState(1);
//			} else if (action.equals(NOTIFICATION_ITEM_BUTTON_NEXT)) {
//				musicState(2);
//			} else if (action.equals(NOTIFICATION_ITEM_BUTTON_LAST)) {
//				musicState(3);
//			} else if (action.equals(NOTIFICATION_ITEM_BUTTON_REPEAT)) {
//				musicState(4);
//			}
//		}
//	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {}
    
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		int niSeek=duraPos*songProgressBar.getProgress()/songProgressBar.getMax();
		Intent intent=new Intent();
		intent.setAction("android.intent.action.currentseek");
		intent.setClass(context,MusicBackService.class);
		intent.putExtra("seekPor",niSeek);
//		Log.i("sysout",niSeek+"seek");
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        context.startService(intent);  
	}
}
