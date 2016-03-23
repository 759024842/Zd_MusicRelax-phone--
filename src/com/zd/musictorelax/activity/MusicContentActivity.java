package com.zd.musictorelax.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.zd.musictorelax.adapter.ContentAdapter;
import com.zd.musictorelax.adapter.ContentAdapter.ViewHolder;
import com.zd.musictorelax.handler.SongFeed;
import com.zd.musictorelax.handler.SongHandler;
import com.zd.musictorelax.service.MusicBackService;
import com.zd.musictorelax.tool.BroTools;
import com.zd.musictorelax.tool.HomeWatcher;
import com.zd.musictorelax.tool.MusicPreference;
import com.zd.musictorelax.tool.HomeWatcher.OnHomePressedListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

public class MusicContentActivity extends Activity implements OnClickListener,OnCheckedChangeListener{
	private ArrayList<HashMap<String, Object>> dataContent;

	private Button btnPlay,btnNext,btnBack,btnAdd,btnReturn;
	private CheckBox cbAdd;
	private TextView txtCurrentName,txtTitle;

	private ListView listView;
	
	private ContentAdapter contentAdapter;
	private SongFeed songFeed;
	private BroadcastReceiver mItemViewListClickReceiver ;
	private String strXmlPath=Environment.getExternalStorageDirectory()+"/ZhuoDunPlatForm/test/musicFun.xml";
//	private String strXmlPre=Environment.getExternalStorageDirectory()+"/xml/dataplay.xml";
    private SeekBar songProgressBar;// 当前进度
    private int currentPor;//当前音乐播放时长
	private int duraPos;   //音乐播放总时长
	public static MusicPreference musicPreference;
    private HomeWatcher mHomeWatcher;  
    private boolean isVisible;
    private BroTools broTools=new BroTools();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.content_layout);
		dataContent = new ArrayList<HashMap<String, Object>>();
		
//		musicPreference = new MusicPreference(this);
//		File file=new File(strXmlPre);
//	    if(file.exists()){
//     	txtCurrentName.setText(musicPreference.getMusicName(this));
//     	if(musicPreference.getPlayState(this))
//     		btnPlay.setBackgroundResource(R.drawable.btn_pause);
//     	else{
//			btnPlay.setBackgroundResource(R.drawable.btn_play);
//     	}
//     	Log.i("sysout", musicPreference.getMusicName(this)+":"+musicPreference.getPlayState(this));
//     	}	    
		
		btnPlay=(Button)findViewById(R.id.btn_main_play);
		btnNext=(Button)findViewById(R.id.btn_main_next);
		btnBack=(Button)findViewById(R.id.btn_main_back);
		btnAdd=(Button)findViewById(R.id.btn_content_add);
		btnReturn=(Button)findViewById(R.id.btn_content_return);
		
		btnPlay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
		btnReturn.setOnClickListener(this);
        
		txtCurrentName=(TextView)findViewById(R.id.txt_music_name);
		txtTitle=(TextView)findViewById(R.id.txt_content_title);
		
        cbAdd=(CheckBox)findViewById(R.id.cbx_content_add);
        cbAdd.setOnCheckedChangeListener(this);
	 	songProgressBar = (SeekBar)findViewById(R.id.music_progress);

		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.play.state");
		intentFilter.addAction("android.intent.action.curSong");
		intentFilter.addAction("android.intent.action.seek");

		 mItemViewListClickReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("action.play.state")) {
					boolean isPlay=intent.getExtras().getBoolean("isPlay");
					if(isPlay)
						btnPlay.setBackgroundResource(R.drawable.btn_pause);
					else
						btnPlay.setBackgroundResource(R.drawable.btn_play);
				}
				else if (intent.getAction().equals("android.intent.action.curSong")){
					 String musicName=intent.getStringExtra("strName");
					 txtCurrentName.setText(musicName);
				}
				else if(intent.getAction().equals("android.intent.action.seek")){
					currentPor=intent.getExtras().getInt("currentPos");
					duraPos=intent.getExtras().getInt("duraPos");
					int seekPor=songProgressBar.getMax()*currentPor/duraPos;
					songProgressBar.setProgress(seekPor);				
				}
			}
		};
		broadcastManager.registerReceiver(mItemViewListClickReceiver,intentFilter);

		listView=(ListView)findViewById(R.id.list_content_lib);		
		listView.setOnItemClickListener(itemLibListener);
		
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
				Log.i("sysout", "KeyEvent.KEYCODE_HOME");
				if(isVisible){
					broTools.broIntentBoolean("android.intent.action.isvisible", "isVisible", true, getApplicationContext());
					isVisible=!isVisible;
					}else{
						  MusicContentActivity.this.finish();
					}
			}

			@Override
			public void onHomeLongPressed() {
				Log.e("sysout", "onHomeLongPressed");
			}
		});
		mHomeWatcher.startWatch();
		//解析歌曲xml文件
		songFeed=getSongFeed();
		showSong();
		showMusicInfo();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHomeWatcher.stopWatch();// 在onPause中停止监听，不然会报错的。
	}
	private StringBuffer sb;
	/** 读取SD卡xml文件 **/
	public void readXmlFromSd(String strName) {
		File f = new File(strName);
		String path = f.getAbsolutePath();
		FileInputStream fileIS = null;
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

	/**解析歌曲xml文件**/
	public SongFeed getSongFeed(){
		readXmlFromSd(strXmlPath);// STR_XML_NAME
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			SongHandler handler = new SongHandler();
			reader.setContentHandler(handler);
			String s1 = sb.toString();
			reader.parse(new InputSource(new StringReader(s1)));
			return handler.getFeed();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private List<Map<String, Object>> dataSong;
	@SuppressWarnings("unchecked")
	private void showSong() {
		if (songFeed == null) {
			return;
		}
		dataSong = new ArrayList<Map<String, Object>>();
		dataSong = songFeed.getAllItemForListView(getApplicationContext());
	}
	/**
	 * 显示歌一类歌曲的歌曲信息
	 */
	private void showMusicInfo(){
		Intent intent=getIntent();
		String kID=intent.getStringExtra("kID");
		String title=intent.getStringExtra("title");
		String musicName=intent.getStringExtra("musicName");
		boolean isPlay=intent.getExtras().getBoolean("isplay");
		if(isPlay)
			btnPlay.setBackgroundResource(R.drawable.btn_pause);
		else
			btnPlay.setBackgroundResource(R.drawable.btn_play);
		txtCurrentName.setText(musicName);
		txtTitle.setText(title);
		Log.i("sysout", kID+"kid");
		for(int i=0;i<dataSong.size();i++){
		  if(((String)dataSong.get(i).get("kID")).equals(kID) && ((String)dataSong.get(i).get("sEnable")).equals("true")){
			  HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("sName",dataSong.get(i).get("sName"));
				map.put("splayTime", dataSong.get(i).get("splayTime"));
				map.put("sFormat", dataSong.get(i).get("sFormat"));
				dataContent.add(map);
		  }
		}
		contentAdapter=new ContentAdapter(dataContent, this);
		listView.setAdapter(contentAdapter);
	}
	
	/**
	 * 当前音乐状态
	 * @param niId
	 */
	private void musicState(int niId){
		Intent intent = new Intent("android.intent.action.Play");
		intent.setClass(MusicContentActivity.this,MusicBackService.class); 
		intent.putExtra("control",niId);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        startService(intent);    
	}
	
	@Override
	public void onClick(View v) {		
		switch(v.getId()){
		case R.id.btn_main_play:
			musicState(1);
			Log.i("sysout", "btn_main_play");
			break;
		case R.id.btn_main_next:
			musicState(2);
			Log.i("sysout", "btn_main_next");
			break;
		case R.id.btn_main_back:
			musicState(3);
			Log.i("sysout", "btn_main_back");
			break;
		case R.id.btn_content_return:
			this.finish();
			break;
		case R.id.btn_content_add:
			if (cbAdd.isChecked()) {
				Log.i("sysout", cbAdd.isChecked() + "cb");
				if (dataContent != null){
					playListMusic();
					cbAdd.setChecked(false);
				for(int i=0;i<dataContent.size();i++){
					ContentAdapter.getIsSelected().put(i, false);
				}
				Toast.makeText(getApplicationContext(), "已添加歌曲", Toast.LENGTH_SHORT).show();
				contentAdapter.notifyDataSetChanged();
				sendBroMessage();
				}
				else
					Toast.makeText(getApplicationContext(), "没有可添加内容", Toast.LENGTH_SHORT).show();
			} else {
				if (dataContent != null && dataPlaya1 != null && dataPlaya1.size()>0) {
					chooseMusic();
					dataPlaya1.clear();
					for(int i=0;i<dataContent.size();i++){
						ContentAdapter.getIsSelected().put(i, false);
					}
					Toast.makeText(getApplicationContext(), "已添加歌曲", Toast.LENGTH_SHORT).show();
					contentAdapter.notifyDataSetChanged();	
					sendBroMessage();
				} else {
					Toast.makeText(getApplicationContext(), "添加内容为空", Toast.LENGTH_SHORT).show();
				}
			}
		}
		    
	}
	
	private int count1 = 0;
	private ArrayList<HashMap<String, Object>> dataPlaya1;
	private int contentPosition=-1;
	private boolean isSelect;
	private OnItemClickListener itemLibListener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
			isSelect=true;
			ViewHolder holder = (ViewHolder) arg1.getTag();
			holder.cb.toggle();
			dataPlaya1 = new ArrayList<HashMap<String, Object>>();
			if (holder.cb.isChecked()){
				ContentAdapter.getIsSelected().put(position,holder.cb.isChecked());
			for (int i = 0; i < dataContent.size(); i++) {
					HashMap<String, Object> mapPlay = new HashMap<String, Object>();
					if (ContentAdapter.getIsSelected().get(i)){
						mapPlay.put("sName", dataContent.get(i).get("sName"));
						mapPlay.put("splayTime", dataContent.get(i).get("splayTime"));	
						mapPlay.put("sFormat", dataContent.get(i).get("sFormat"));
					} 
					dataPlaya1.add(mapPlay);		
				} 
			count1++;
			if(count1!=dataContent.size()){
		    	cbAdd.setChecked(false);									
			}
			}else {
				ContentAdapter.getIsSelected().put(position,false);
				    for(int i=0;i<dataContent.size();i++){
				    	HashMap<String, Object> mapPlay = new HashMap<String, Object>();
				    if (ContentAdapter.getIsSelected().get(i)==true){
					mapPlay.put("sName", dataContent.get(i).get("sName"));
					mapPlay.put("splayTime",  dataContent.get(i).get("splayTime"));
					mapPlay.put("sFormat", dataContent.get(i).get("sFormat"));
				    }
				    dataPlaya1.add(mapPlay);
				    }
				    count1--;
				    if(count1!=dataContent.size()){
				    	cbAdd.setChecked(false);
						ContentAdapter.getIsSelected().put(position,false);
				    	contentPosition=position;
					}
				}
			Log.i("sysout", dataPlaya1+"play1"+count1);
			contentAdapter.notifyDataSetChanged();			
		}
	};

	/***
	 * 发送数据广播
	 */	
	public void sendBroMessage(){
		Log.i("sysout", "点击播放");
		Intent intent = new Intent();
		intent.setAction("android.intent.action.Lib");
		intent.putExtra("datac",dataPlay);
		intent.setClassName("com.zd.musictorelax.service","com.zd.musictorelax.service.MusicBackService");
		MusicContentActivity.this.startService(intent);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
	}
	
	private  ArrayList<HashMap<String, Object>> dataPlay;
	/** 可播放一类歌曲中的所有歌曲 **/
	public void playListMusic() {
		dataPlay = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < dataContent.size(); i++) {
			HashMap<String, Object> mapPlay = new HashMap<String, Object>();
			mapPlay.put("sName", dataContent.get(i).get("sName"));
			mapPlay.put("splayTime",  dataContent.get(i).get("splayTime"));
			mapPlay.put("sFormat", dataContent.get(i).get("sFormat"));
			dataPlay.add(mapPlay);
		}
	}

	/** 选择多首歌曲 **/
	public void chooseMusic() {
		dataPlay = new ArrayList<HashMap<String, Object>>();
		for(int i=0;i<dataPlaya1.size();i++){
			HashMap<String,Object> map=new HashMap<String,Object>();
			if(dataPlaya1.get(i).get("sName")!=null){
					map.put("sName", dataPlaya1.get(i).get("sName"));
					map.put("splayTime", dataPlaya1.get(i).get("splayTime"));
					map.put("sFormat", dataPlaya1.get(i).get("sFormat"));
					dataPlay.add(map);
			}			
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 多选框的判断选择
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean arg1) {
		switch (buttonView.getId()) {
		case R.id.cbx_content_add:
			for (int i = 0; i < dataContent.size(); i++) {
				if (cbAdd.isChecked()) {
					ContentAdapter.getIsSelected().put(i, true);
					count1 = dataContent.size();
				} else {
					if (isSelect) {
						if (i == contentPosition) {
							ContentAdapter.getIsSelected().put(i, false);
						} else {
							ContentAdapter.getIsSelected().put(i, true);
						}
						contentPosition = -1;
					} else {
						ContentAdapter.getIsSelected().put(i, false);				
						count1=0;
					}
				}
			}
			isSelect = false;
			contentAdapter.notifyDataSetChanged();
			break;
		}
	}
}
