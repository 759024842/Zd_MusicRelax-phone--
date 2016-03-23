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
import com.zd.musictorelax.activity.MusicContentActivity;
import com.zd.musictorelax.activity.R;
import com.zd.musictorelax.handler.SongKFeed;
import com.zd.musictorelax.handler.SongKHandler;
import com.zd.musictorelax.tool.MusicPreference;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "ShowToast", "HandlerLeak" })
public class LibFragment extends Fragment {
	private GridView griView;
	private Context context;
	private SongKFeed songkFeed;
	private BroadcastReceiver mItemViewListClickReceiver ;
	private boolean isPlay;
	private String musicName;
	public static MusicPreference musicPreference;
	private String strXmlPre=Environment.getExternalStorageDirectory()+"/xml/dataplay.xml";

	@SuppressWarnings("unused")
	private int[] imgs1 = { R.drawable.ic_launcher, R.drawable.ic_launcher,
			                R.drawable.ic_launcher, R.drawable.ic_launcher,
			                R.drawable.ic_launcher, R.drawable.ic_launcher			                
			                };
	private String strXmlPath=Environment.getExternalStorageDirectory()+"/ZhuoDunPlatForm/test/musicFun.xml";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context = getActivity();
		View view = inflater.inflate(R.layout.lib_fragment, container, false);
		
		musicPreference = new MusicPreference(context);

		 LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("action.play.state");
			intentFilter.addAction("android.intent.action.curSong");

			 mItemViewListClickReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (intent.getAction().equals("action.play.state")) {
						isPlay=intent.getExtras().getBoolean("isPlay");
					}
					else if (intent.getAction().equals("android.intent.action.curSong")){
						musicName=intent.getStringExtra("strName");
					}
				}
			};
			broadcastManager.registerReceiver(mItemViewListClickReceiver,intentFilter);
			File file=new File(strXmlPre);
		 	if(file.exists()){
		 		musicName=musicPreference.getMusicName(context);
		 		if(musicPreference.getPlayState(context))
		 			isPlay=true;
		 			else
		 			isPlay=false;
		 	}	    
		// 初始化相关变量
		griView = (GridView) view.findViewById(R.id.gri_main_lib);
		griView.setOnItemClickListener(new GridMonitor());
		//解析歌曲类型xml文件
		songkFeed=getSongKFeed();
		showSongK();
		showSongKInfo();
		return view;
	}
	private StringBuffer sb;
	/** 读取SD卡xml文件 **/
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

	/** 解析方案XML文件 **/
	public SongKFeed getSongKFeed() {
		readXmlFromSd(strXmlPath);// STR_XML_NAME
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			SongKHandler handler = new SongKHandler();
			reader.setContentHandler(handler);
			if(sb!=null){
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
	
	private List<Map<String, Object>> dataSongK;
	@SuppressWarnings("unchecked")
	public void showSongK() {
		if (songkFeed == null) {
			return;
		}
		dataSongK = new ArrayList<Map<String, Object>>();
		dataSongK = songkFeed.getAllItemForListView(context);
	}
	
 	private List<HashMap<String, Object>> dataInfo;
    /**
     * 显示歌曲类型信息
     */
	public void showSongKInfo() {
		dataInfo = new ArrayList<HashMap<String, Object>>();
		if (dataSongK != null) {
			for (int i = 0; i < dataSongK.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("img", R.drawable.kind1);
				map.put("name",dataSongK.get(i).get("kName"));
				dataInfo.add(map);
			}
			SimpleAdapter sa = new SimpleAdapter(context, dataInfo,
					                             R.layout.gri_kind_item, new String[] {"img", "name"},
					                             new int[] { R.id.img_gri_kind, R.id.txt_gri_kind });
			griView.setAdapter(sa);
		}
	}
	
	/**
	 * GridView监听事件
	 */
	public class GridMonitor implements OnItemClickListener {
		@SuppressLint("NewApi")
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
            int niKid=position+1;
            Intent intent=new Intent();
			intent.setClass(context, MusicContentActivity.class);
			//intent.putExtra("kID", (String)dataSongK.get(position).get("kID"));
			intent.putExtra("kID", niKid+"");
			intent.putExtra("title", dataSongK.get(position).get("kName")+"");
			intent.putExtra("musicName", musicName);
			intent.putExtra("isplay", isPlay);
			startActivity(intent);
		}
	}
}
