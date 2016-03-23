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
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.zd.musictorelax.activity.R;
import com.zd.musictorelax.handler.RelaxSFeed;
import com.zd.musictorelax.handler.RelaxSHandler;
import com.zd.musictorelax.handler.RelaxSSFeed;
import com.zd.musictorelax.handler.RelaxSSHandler;
import com.zd.musictorelax.handler.SongFeed;
import com.zd.musictorelax.handler.SongHandler;
import com.zd.musictorelax.service.MusicBackService;
import com.zd.musictorelax.tool.MusicPreference;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PlanFragment extends Fragment implements OnClickListener {
	private GridView griView; // 音乐类型
	private ArrayList<HashMap<String, Object>> dataPlan;// 音乐方案集合
	private int[] imgs1 = { R.drawable.plan1, R.drawable.plan2, R.drawable.plan3, R.drawable.plan4, R.drawable.plan5,
			R.drawable.plan6 };
	private Context context;
	private TextView txtCurrentName;

	private Button btnPlay, btnNext, btnBack;
	private SeekBar songProgressBar;// 当前进度

	public RelaxSFeed relaxsFeed;
	public RelaxSSFeed relaxssFeed;
	public SongFeed songFeed;
	private String strXmlPath = Environment.getExternalStorageDirectory() + "/ZhuoDunPlatForm/test/musicFun.xml";
	private String strXmlPre = Environment.getExternalStorageDirectory() + "/xml/dataplay.xml";

	private BroadcastReceiver mItemViewListClickReceiver;
	private int currentPor;
	private int duraPos;
	private boolean isPlay;
	public static MusicPreference musicPreference;
	ProgressDialog pd;
	ProgressBar pb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		View view = inflater.inflate(R.layout.plan_fragment, container, false);

		musicPreference = new MusicPreference(context);

		btnPlay = (Button) view.findViewById(R.id.btn_main_play);
		btnNext = (Button) view.findViewById(R.id.btn_main_next);
		btnBack = (Button) view.findViewById(R.id.btn_main_back);

		btnPlay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnBack.setOnClickListener(this);

		txtCurrentName = (TextView) view.findViewById(R.id.txt_music_name);
		songProgressBar = (SeekBar) view.findViewById(R.id.music_progress);
		pb = (ProgressBar) view.findViewById(R.id.pb_music_name);
		// songProgressBar.setOnSeekBarChangeListener(this);
		File file = new File(strXmlPre);
		if (file.exists()) {
			txtCurrentName.setText(musicPreference.getMusicName(context));
			if (musicPreference.getPlayState(context))
				btnPlay.setBackgroundResource(R.drawable.btn_pause);
			else
				btnPlay.setBackgroundResource(R.drawable.btn_play);
			// Log.i("sysout",
			// musicPreference.getMusicName(context)+":"+musicPreference.getPlayState(context));
		}
		// 解析 xml文件
		relaxsFeed = getRelaxSFeed();
		showRelaxs();
		relaxssFeed = getRelaxSSFeed();
		showRelaxss();
		songFeed = getSongFeed();
		showSong();
		// 添加并且显示
		griView = (GridView) view.findViewById(R.id.gri_main_music);
		griView.setOnItemClickListener(new GridMonitor());
		dataPlan = new ArrayList<HashMap<String, Object>>();

		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.play.state");
		intentFilter.addAction("android.intent.action.curSong");
		intentFilter.addAction("android.intent.action.seek");
		// intentFilter.addAction("android.intent.action.loading");

		mItemViewListClickReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("action.play.state")) {
					isPlay = intent.getExtras().getBoolean("isPlay");
					if (isPlay)
						btnPlay.setBackgroundResource(R.drawable.btn_pause);
					else
						btnPlay.setBackgroundResource(R.drawable.btn_play);
				} else if (intent.getAction().equals("android.intent.action.curSong")) {
					String musicName = intent.getStringExtra("strName");
					txtCurrentName.setText(musicName);
					// pd.dismiss();
					// pb.setVisibility(View.GONE);
				} else if (intent.getAction().equals("android.intent.action.seek")) {
					currentPor = intent.getExtras().getInt("currentPos");
					duraPos = intent.getExtras().getInt("duraPos");
					int seekPor = songProgressBar.getMax() * currentPor / duraPos;
					songProgressBar.setProgress(seekPor);
				}
				// else
				// if(intent.getAction().equals("android.intent.action.loading")){
				// pd = new ProgressDialog(getActivity());
				// pd.setTitle("音乐加载中");
				// pd.setMessage("请稍等...");
				// pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				// pd.setCancelable(false);
				// pd.show();
				// pb.setVisibility(View.VISIBLE);
				// }
			}
		};
		broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
		showInfo();
		return view;
	}

	private StringBuffer sb;

	/** 读取SD卡xml文件 **/
	public void readXmlFromSd(String strName) {
		File f = new File(strName);
		String path = f.getAbsolutePath();
		FileInputStream fileIS = null;
		if (f.exists()) {
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

	/** 解析方案XML文件 **/
	public RelaxSFeed getRelaxSFeed() {
		readXmlFromSd(strXmlPath);// STR_XML_NAME
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			RelaxSHandler handler = new RelaxSHandler();
			reader.setContentHandler(handler);
			if (sb != null) {
				String s1 = sb.toString();
				reader.parse(new InputSource(new StringReader(s1)));
			}
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

	private List<Map<String, Object>> dataRelaxs;

	@SuppressWarnings("unchecked")
	public void showRelaxs() {
		if (relaxsFeed == null) {
			return;
		}
		dataRelaxs = new ArrayList<Map<String, Object>>();
		dataRelaxs = relaxsFeed.getAllItemForListView(context);
	}

	/** 解析方案类型文件 **/
	private RelaxSSFeed getRelaxSSFeed() {
		readXmlFromSd(strXmlPath);// STR_XML_NAME
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			RelaxSSHandler handler = new RelaxSSHandler();
			reader.setContentHandler(handler);
			if (sb != null) {
				String s1 = sb.toString();
				reader.parse(new InputSource(new StringReader(s1)));
			}
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

	private List<Map<String, Object>> dataRelaxss;

	@SuppressWarnings("unchecked")
	public void showRelaxss() {
		if (relaxssFeed == null) {
			return;
		}
		dataRelaxss = new ArrayList<Map<String, Object>>();
		dataRelaxss = relaxssFeed.getAllItemForListView(context);
	}

	/** 解析歌曲xml文件 **/
	public SongFeed getSongFeed() {
		readXmlFromSd(strXmlPath);// STR_XML_NAME
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			SongHandler handler = new SongHandler();
			reader.setContentHandler(handler);
			if (sb != null) {
				String s1 = sb.toString();
				reader.parse(new InputSource(new StringReader(s1)));
			}
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
		dataSong = songFeed.getAllItemForListView(context);
	}

	/**
	 * 显示gridview的内容
	 */
	public void showInfo() {
		dataPlan = new ArrayList<HashMap<String, Object>>();
		if (dataRelaxss != null) {
			for (int i = 0; i < dataRelaxs.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("img", imgs1[i]);
				map.put("name", dataRelaxs.get(i).get("rName"));
				dataPlan.add(map);
			}
			SimpleAdapter sa = new SimpleAdapter(context, dataPlan, R.layout.gri_item, new String[] { "img", "name" },
					new int[] { R.id.img_gri_pic, R.id.txt_gri_result });
			griView.setAdapter(sa);
		}
	}

	// public static ArrayList<HashMap<String, Object>> dataLib=new
	// ArrayList<HashMap<String,Object>>();//音乐方案集合
	private ArrayList<HashMap<String, Object>> dataLib = new ArrayList<HashMap<String, Object>>();

	/**
	 * 
	 * GridView监听事件
	 * 
	 */
	public class GridMonitor implements OnItemClickListener {
		@SuppressLint("NewApi")
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
			if (dataLib != null) {
				dataLib.clear();
			}
			int currentID = position + 1;
			for (int j = 0; j < dataRelaxss.size(); j++) {
				if (Integer.valueOf((String) dataRelaxss.get(j).get("rID")) == currentID
						&& ((String) dataRelaxss.get(j).get("Enable")).equals("true")) {
					for (int i = 0; i < dataSong.size(); i++) {
						if (((String) dataRelaxss.get(j).get("sID")).equals((String) dataSong.get(i).get("sID"))) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("sName", dataSong.get(i).get("sName"));
							map.put("splayTime", dataSong.get(i).get("splayTime"));
							map.put("sFormat", dataSong.get(i).get("sFormat"));
							dataLib.add(map);
						}
					}
				}
			}
			Intent intent = new Intent();
			intent.putExtra("datac", dataLib);
			intent.setAction("android.intent.action.Lib");
			// intent.setClass(context,MusicBackService.class);
			// context.startService(intent);

			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
		}
	}

	// private void PopupBox() {
	// final ProgressDialog pd = new ProgressDialog(context);
	// pd.setTitle("音乐加载中");
	// pd.setMessage("请稍等...");
	// pd.setProgressStyle(ProgressDialog.STYLE_SPINNER); //
	// ProgressDialog.STYLE_SPINNER（圆形旋转）
	// pd.setMax(100);
	// pd.setCancelable(false);
	// pd.show();
	// new Thread(new Runnable() {
	// public void run() {
	// while (true) {
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// }
	//
	// }
	// }
	// }).start();
	// }

	@Override
	public void onStart() {
		super.onStart();
		// File file=new File(strXmlPre);
		// if(file.exists()){
		// txtCurrentName.setText(musicPreference.getMusicName(context));
		// if(musicPreference.getPlayState(context))
		// btnPlay.setBackgroundResource(R.drawable.btn_pause);
		// else
		// btnPlay.setBackgroundResource(R.drawable.btn_play);
		// Log.i("sysout",
		// musicPreference.getMusicName(context)+":"+musicPreference.getPlayState(context));
		// }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 当前音乐状态
	 * 
	 * @param niId
	 */
	private void musicState(int niId) {
		Intent intent = new Intent("android.intent.action.Play");
		intent.setClass(context, MusicBackService.class);
		intent.putExtra("control", niId);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		context.startService(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
		}
	}
}