package com.zd.musictorelax.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.zd.musictorelax.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class ContentAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, Object>> list;
	@SuppressWarnings("unused")
	private Context context;
	private LayoutInflater inflater = null;
	private static HashMap<Integer, Boolean> isSelected;  
	public ContentAdapter(ArrayList<HashMap<String, Object>> list,Context context){
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		initDate(); 
	}

	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_content_item, null);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cbx_list_content);  
			holder.txtName = (TextView) convertView
					.findViewById(R.id.txt_listcontent_name);
			holder.txtTime = (TextView) convertView
					.findViewById(R.id.txt_listcontent_time);
			holder.btnLoad = (Button) convertView
					.findViewById(R.id.btn_listcontent_load);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
        holder.cb.setChecked(getIsSelected().get(position));
		holder.txtName.setText(list.get(position).get("sName") + "");
		holder.txtTime.setText(list.get(position).get("splayTime") + "");
		holder.btnLoad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("sysout", "ÒÑÏÂÔØ");
			}
		});
		return convertView;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		ContentAdapter.isSelected = isSelected;
	}

	public static class ViewHolder {
    	public CheckBox cb; 
		public TextView txtName;
		public TextView txtTime;
		public Button btnLoad;
	}
}
